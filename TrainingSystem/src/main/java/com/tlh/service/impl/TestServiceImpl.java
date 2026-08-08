/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.Test;
import com.tlh.repository.TestRepository;
import com.tlh.service.QuestionOptionService;
import com.tlh.service.QuestionService;
import com.tlh.service.TestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class TestServiceImpl implements TestService{

    @Autowired
    private TestRepository testRepo;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionOptionService questionOptionService;
    
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

    private void validate(long testId){
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
        t.setIsActive(false);
        this.testRepo.saveOrUpdate(t);
        return t;
    }

    @Override
    public Test updateTest(Test t) {
        validateFields(t);
        if (t.getIsActive())
            validate(t.getId());
        this.testRepo.saveOrUpdate(t);
        return t;
    }
    
}
