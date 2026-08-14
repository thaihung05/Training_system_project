/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.Test;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.repository.TestRepository;
import com.tlh.service.QuestionOptionService;
import com.tlh.service.QuestionService;
import com.tlh.service.TestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class TestServiceImpl implements TestService{

    @Autowired
    private TestRepository testRepo;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionOptionService questionOptionService;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private TestAttemptRepository testAttemptRepo;
    
    private void validateFields(Test t){
        if (t.getTitle() == null || t.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Tên bài kiểm tra không được để trống");
        if (t.getTitle().trim().length() > 200)
            throw new IllegalArgumentException("Tên bài kiểm tra tối đa 200 ký tự");
        t.setTitle(t.getTitle().trim());
        if (t.getPassScore() < 0 || t.getPassScore() > 100)
            throw new IllegalArgumentException("Điểm đạt phải trong khoảng 0-100");
        if (t.getMaxAttempts() < 1)
            throw new IllegalArgumentException("Số lần làm bài tối đa phải từ 1 trở lên");
    }

    @Override
    public void validateForActivation(long testId){
        List<Question> activeQuestions = this.questionService.getActiveByTest(testId);
        if (activeQuestions.isEmpty())
            throw new IllegalArgumentException("Bài kiểm tra chưa có câu hỏi nào đang hoạt động, không thể kích hoạt");
        for (Question q : activeQuestions) {
            List<QuestionOption> options = this.questionOptionService.getByQuestion(q.getId());
            if (options.size() < 2)
                throw new IllegalArgumentException("Câu hỏi " + q.getId() + " chưa đủ đáp án, không thể kích hoạt bài kiểm tra");
            int correctCount = 0;
            for (QuestionOption o : options)
                if (o.getIsCorrect())
                    correctCount++;
            if (correctCount!=1)
                throw new IllegalArgumentException("Câu hỏi " + q.getId() + " chưa có đáp án đúng, không thể kích hoạt bài kiểm tra");
        }
    }
    
    @Override
    public List<Test> getByCourse(long courseId) {
        return this.testRepo.getByCourse(courseId);
    }

    @Override
    public Test getById(long id) {
        return this.testRepo.getById(id);
    }

    @Override
    public Test addTest(Test t) {
        validateFields(t);
        if (t.getCourseId() == null || t.getCourseId().getId() == null) {
            throw new IllegalArgumentException("Bài kiểm tra phải thuộc một khóa học");
        }
        if (this.enrollmentRepo.hasEnrollments(t.getCourseId().getId())) {
            throw new IllegalArgumentException("Khóa học đã có người ghi danh, không thể thêm bài kiểm tra. Hãy tạo khóa học mới nếu cần thay đổi nội dung.");
        }
        t.setIsActive(false);
        this.testRepo.saveOrUpdate(t);
        return t;
    }

    @Override
    public Test updateTest(Test t) {
        validateFields(t);
        Test existing = this.testRepo.getById(t.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy bài kiểm tra");
        }

        boolean passScoreChanged = existing.getPassScore() != t.getPassScore();
        boolean maxAttemptsChanged = existing.getMaxAttempts() != t.getMaxAttempts();
        boolean activeChanged = existing.getIsActive() != t.getIsActive();

        if (this.testAttemptRepo.hasAttempts(t.getId())
                && (passScoreChanged || maxAttemptsChanged || activeChanged)) {
            throw new IllegalArgumentException("Bài kiểm tra đã có lượt làm bài, không thể đổi điểm đạt, số lần làm hoặc trạng thái.");
        }
        if (this.enrollmentRepo.hasEnrollments(existing.getCourseId().getId())
                && (passScoreChanged || maxAttemptsChanged || activeChanged)) {
            throw new IllegalArgumentException("Khóa học đã có người ghi danh, không thể thay đổi cấu hình bài kiểm tra.");
        }
        if (t.getIsActive()) {
            validateForActivation(t.getId());
        }
        this.testRepo.saveOrUpdate(t);
        return t;
    }
    
}
