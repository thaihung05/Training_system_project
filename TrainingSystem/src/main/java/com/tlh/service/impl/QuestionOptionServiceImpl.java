/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.Test;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.QuestionOptionRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.repository.TestRepository;
import com.tlh.service.QuestionOptionService;
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
public class QuestionOptionServiceImpl implements QuestionOptionService {

    @Autowired
    private QuestionOptionRepository questionOptionRepo;

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
                    "Khóa học đã có người ghi danh, không thể thay đổi đáp án. Hãy tạo khóa học mới nếu cần cập nhật nội dung.");
        }
        if (this.testAttemptRepo.hasAttempts(testId)) {
            throw new IllegalArgumentException(
                    "Bài kiểm tra này đã có người làm bài, không thể thay đổi đáp án. Hãy tạo bài kiểm tra mới nếu cần thay đổi nội dung.");
        }
    }

    @Override
    public List<QuestionOption> getByQuestion(long questionId) {
        return this.questionOptionRepo.getByQuestion(questionId);
    }

    @Override
    public QuestionOption getById(long id) {
        return this.questionOptionRepo.getById(id);
    }

    @Override
    public QuestionOption addOption(QuestionOption o) {
        validateText(o);
        assertTestNotLocked(o.getQuestionId().getTestId().getId());
        o.setIsCorrect(false);
        int maxOrder = this.questionOptionRepo.getMaxOrderIndex(o.getQuestionId().getId());
        o.setOrderIndex(maxOrder + 1);
        this.questionOptionRepo.saveOrUpdate(o);
        return o;
    }

    @Override
    public QuestionOption updateOption(QuestionOption o) {
        validateText(o);
        assertTestNotLocked(o.getQuestionId().getTestId().getId());
        this.questionOptionRepo.saveOrUpdate(o);
        return o;
    }

    @Override
    public void deleteOption(long id) {
        QuestionOption target = this.questionOptionRepo.getById(id);
        if (target != null) {
            Question question = target.getQuestionId();
            if (question != null) {
                assertTestNotLocked(question.getTestId().getId());
            }
            if (question != null && question.getIsActive()) {
                Test test = this.testRepo.getById(question.getTestId().getId());
                if (test != null && test.getIsActive()) {
                    List<QuestionOption> siblings = this.questionOptionRepo.getByQuestion(question.getId());
                    long remainingCount = siblings.size() - 1;
                    if (remainingCount < 2) {
                        throw new IllegalArgumentException("Bài kiểm tra đang hoạt động, câu hỏi phải còn ít nhất 2 đáp án. Hãy tắt kích hoạt bài kiểm tra trước.");
                    }
                    boolean remainingHasCorrect = false;
                    for (QuestionOption sibling : siblings) {
                        if (!sibling.getId().equals(id) && sibling.getIsCorrect()) {
                            remainingHasCorrect = true;
                            break;
                        }
                    }
                    if (!remainingHasCorrect) {
                        throw new IllegalArgumentException("Bài kiểm tra đang hoạt động, câu hỏi phải còn đúng 1 đáp án đúng. Hãy tắt kích hoạt bài kiểm tra trước.");
                    }
                }
            }
        }
        this.questionOptionRepo.delete(id);
    }

    @Override
    public List<QuestionOption> setCorrectOption(long questionId, long optionId) {
        QuestionOption target = this.questionOptionRepo.getById(optionId);
        if (target == null || target.getQuestionId() == null
                || !target.getQuestionId().getId().equals(questionId)) {
            throw new IllegalArgumentException("Đáp án không thuộc câu hỏi này");
        }
        assertTestNotLocked(target.getQuestionId().getTestId().getId());
        this.questionOptionRepo.clearCorrectForQuestion(questionId);
        target.setIsCorrect(true);
        this.questionOptionRepo.saveOrUpdate(target);
        return this.questionOptionRepo.getByQuestion(questionId);
    }

    private void validateText(QuestionOption o) {
        if (o.getOptionText() == null || o.getOptionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung đáp án không được để trống");
        }
        if (o.getOptionText().trim().length() > 500) {
            throw new IllegalArgumentException("Nội dung đáp án tối đa 500 ký tự");
        }
        o.setOptionText(o.getOptionText().trim());
    }
}
