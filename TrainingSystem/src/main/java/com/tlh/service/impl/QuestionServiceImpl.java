/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.Test;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.QuestionRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.repository.TestRepository;
import com.tlh.service.QuestionOptionService;
import com.tlh.service.QuestionService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{

    private static final long MAX_IMPORT_SIZE = 10L * 1024 * 1024;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private QuestionOptionService questionOptionService;

    @Autowired
    private TestRepository testRepo;

    @Autowired
    private TestAttemptRepository testAttemptRepo;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    private void assertTestNotLocked(long testId) {
        Test test = this.testRepo.getById(testId);
        if (test == null) {
            throw new IllegalArgumentException("Không tìm thấy bài kiểm tra");
        }
        if (this.enrollmentRepo.hasEnrollments(test.getCourseId().getId())) {
            throw new IllegalArgumentException(
                    "Khóa học đã có người ghi danh, không thể thay đổi câu hỏi. Hãy tạo khóa học mới nếu cần cập nhật nội dung.");
        }
        if (this.testAttemptRepo.hasAttempts(testId)) {
            throw new IllegalArgumentException(
                    "Bài kiểm tra này đã có người làm bài, không thể thêm/sửa/xoá câu hỏi. Hãy tạo bài kiểm tra mới nếu cần thay đổi nội dung.");
        }
    }

    private void validateContent(Question q) {
        if (q.getContent() == null || q.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung câu hỏi không được để trống");
        }
        if (q.getContent().trim().length() > 65535) {
            throw new IllegalArgumentException("Nội dung câu hỏi quá dài");
        }
        q.setContent(q.getContent().trim());
    }

    private void assertActiveQuestion(Test test) {
        if (test == null || !test.getIsActive())
            return;
        long remaining = this.questionRepo.getActiveByTest(test.getId()).size() - 1;
        if (remaining <= 0) {
            throw new IllegalArgumentException("Bài kiểm tra đang hoạt động, không thể để trống câu hỏi đang hoạt động. Hãy tắt kích hoạt bài kiểm tra trước.");
        }
    }
    
    @Override
    public List<Question> getByTest(long testId, Integer page, Integer size) {
        return this.questionRepo.getByTest(testId, page, size);
    }

    @Override
    public List<Question> getActiveByTest(long testId) {
        return this.questionRepo.getActiveByTest(testId);
    }

    @Override
    public Question getById(long id) {
        return this.questionRepo.getById(id);
    }

    @Override
    public Question addQuestion(Question q) {
        validateContent(q);
        assertTestNotLocked(q.getTestId().getId());
        Test test = this.testRepo.getById(q.getTestId().getId());
        q.setIsActive(test == null || !test.getIsActive());
        this.questionRepo.saveOrUpdate(q);
        return q;
    }

    @Override
    public Question updateQuestion(Question q) {
        validateContent(q);
        Question existing = this.questionRepo.getById(q.getId());
        assertTestNotLocked(q.getTestId().getId());
        if (existing != null && existing.getIsActive() && !q.getIsActive())
            assertActiveQuestion(this.testRepo.getById(q.getTestId().getId()));
        if (existing != null && !existing.getIsActive() && q.getIsActive())
            validateQuestionForActivation(existing);
        this.questionRepo.saveOrUpdate(q);
        return q;
    }

    @Override
    public void deleteQuestion(long id) {
        Question existing = this.questionRepo.getById(id);
        if (existing != null) {
            assertTestNotLocked(existing.getTestId().getId());
            if (existing.getIsActive())
                assertActiveQuestion(this.testRepo.getById(existing.getTestId().getId()));
        }
        this.questionRepo.delete(id);
    }

    @Override
    public List<Map<String, Object>> getQuestionsForCompose(long testId, Integer page, Integer size) {
        List<Question> questions = this.questionRepo.getByTest(testId, page, size);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("content", q.getContent());
            qMap.put("isActive", q.getIsActive());

            List<QuestionOption> options = this.questionOptionService.getByQuestion(q.getId());
            List<Map<String, Object>> optList = new ArrayList<>();
            for (QuestionOption o : options) {
                Map<String, Object> oMap = new LinkedHashMap<>();
                oMap.put("id", o.getId());
                oMap.put("optionText", o.getOptionText());
                oMap.put("orderIndex", o.getOrderIndex());
                oMap.put("isCorrect", o.getIsCorrect());
                optList.add(oMap);
            }
            qMap.put("options", optList);
            result.add(qMap);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> bulkImportQuestions(Test test, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn file Excel để nhập");
        }
        if (file.getSize() > MAX_IMPORT_SIZE) {
            throw new IllegalArgumentException("File Excel tối đa 10 MB");
        }
        assertTestNotLocked(test.getId());
        List<Map<String, Object>> results = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String content = getCellString(formatter, row, 0);
                List<String> optionTexts = new ArrayList<>();
                for (int col = 1; col <= 4; col++) {
                    String opt = getCellString(formatter, row, col);
                    if (opt != null) {
                        optionTexts.add(opt);
                    }
                }
                String correctIndexStr = getCellString(formatter, row, 5);

                if (content == null && optionTexts.isEmpty()) {
                    continue;
                }

                Map<String, Object> rowResult = new LinkedHashMap<>();
                rowResult.put("row", i + 1);
                if (content == null) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + ": Nội dung câu hỏi không được để trống");
                }
                if (content.length() > 65535) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + ": Nội dung câu hỏi quá dài");
                }
                if (optionTexts.size() < 2) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + ": Phải có ít nhất 2 đáp án");
                }
                for (String optionText : optionTexts) {
                    if (optionText.length() > 500) {
                        throw new IllegalArgumentException("Dòng " + (i + 1) + ": Nội dung đáp án tối đa 500 ký tự");
                    }
                }

                int correctIndex;
                try {
                    correctIndex = Integer.parseInt(correctIndexStr == null ? "" : correctIndexStr.trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + ": Cột 'Đáp án đúng' phải là số thứ tự đáp án");
                }
                if (correctIndex < 1 || correctIndex > optionTexts.size()) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + ": Số thứ tự đáp án đúng không hợp lệ");
                }

                Question q = new Question();
                q.setContent(content);
                q.setTestId(test);
                this.addQuestion(q);

                QuestionOption correctOption = null;
                for (int idx = 0; idx < optionTexts.size(); idx++) {
                    QuestionOption o = new QuestionOption();
                    o.setOptionText(optionTexts.get(idx));
                    o.setQuestionId(q);
                    QuestionOption saved = this.questionOptionService.addOption(o);
                    if (idx == correctIndex - 1) {
                        correctOption = saved;
                    }
                }
                this.questionOptionService.setCorrectOption(q.getId(), correctOption.getId());

                rowResult.put("status", "success");
                results.add(rowResult);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Không đọc được file Excel: " + e.getMessage());
        }
        return results;
    }

    private String getCellString(DataFormatter formatter, Row row, int idx) {
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(idx);
        if (cell == null) {
            return null;
        }
        String v = formatter.formatCellValue(cell).trim();
        return v.isEmpty() ? null : v;
    }

    private void validateQuestionForActivation(Question question) {
        Test test = this.testRepo.getById(question.getTestId().getId());
        if (test == null || !test.getIsActive()) {
            return;
        }
        List<QuestionOption> options = this.questionOptionService.getByQuestion(question.getId());
        if (options.size() < 2) {
            throw new IllegalArgumentException("Câu hỏi phải có ít nhất 2 đáp án trước khi bật");
        }
        int correctCount = 0;
        for (QuestionOption option : options) {
            if (option.getIsCorrect()) {
                correctCount++;
            }
        }
        if (correctCount != 1) {
            throw new IllegalArgumentException("Câu hỏi phải có đúng 1 đáp án đúng trước khi bật");
        }
    }

}
