/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.AttemptAnswer;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.Test;
import com.tlh.pojo.TestAttempt;
import com.tlh.pojo.User;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.service.CertificateService;
import com.tlh.service.NotificationService;
import com.tlh.service.PointTransactionService;
import com.tlh.service.QuestionOptionService;
import com.tlh.service.QuestionService;
import com.tlh.service.TestAttemptService;
import com.tlh.service.TestService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class TestAttemptServiceImpl implements TestAttemptService{

    @Autowired
    private TestAttemptRepository testAttemptRepo;

    @Autowired
    private TestService testService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionOptionService questionOptionService;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private CertificateService certificateService;


    private Map<String, Object> buildAttemptDetail(TestAttempt attempt, List<AttemptAnswer> answers) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", attempt.getId());
        result.put("attemptNo", attempt.getAttemptNo());
        result.put("score", attempt.getScore());
        result.put("passed", attempt.getPassed());
        result.put("startedAt", attempt.getStartedAt());
        result.put("submittedAt", attempt.getSubmittedAt());
        
        List<Map<String, Object>> answerList = new ArrayList<>();
        for (AttemptAnswer a : answers) {
            Map<String, Object> aMap = new LinkedHashMap<>();
            aMap.put("questionId", a.getQuestionId().getId());
            aMap.put("selectedOptionId", a.getSelectedOptionId() == null ? null : a.getSelectedOptionId().getId());
            aMap.put("isCorrect", a.getIsCorrect());
            answerList.add(aMap);
        }
        result.put("answers", answerList);
        return result;
    }
    
    @Override
    public TestAttempt startAttempt(long testId, User caller) {
        Test t = this.testService.getById(testId);
        if (t == null) {
            throw new IllegalArgumentException("Không tìm thấy bài kiểm tra");
        }
        if (!t.getIsActive()) {
            throw new IllegalArgumentException("Bài kiểm tra chưa được kích hoạt");
        }

        Enrollment enrollment = this.enrollmentRepo.getByCourseAndUser(t.getCourseId().getId(), caller.getId());
        if (enrollment == null) {
            throw new IllegalArgumentException("Bạn chưa được ghi danh khóa học này");
        }

        List<TestAttempt> existing = this.testAttemptRepo.getByUserAndTest(caller.getId(), testId);
        if (existing.size() >= t.getMaxAttempts()) {
            throw new IllegalArgumentException("Đã đạt số lần làm bài tối đa (" + t.getMaxAttempts() + ")");
        }

        TestAttempt a = new TestAttempt();
        a.setTestId(t);
        a.setUserId(caller);
        a.setAttemptNo(existing.size() + 1);
        a.setScore(0);
        a.setPassed(false);
        this.testAttemptRepo.saveOrUpdate(a);
        return a;
    }

    @Override
    public List<Map<String, Object>> getQuestionsForAttempt(long testId) {
        List<Question> questions = this.questionService.getActiveByTest(testId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("content", q.getContent());

            List<QuestionOption> options = this.questionOptionService.getByQuestion(q.getId());
            List<Map<String, Object>> optList = new ArrayList<>();
            for (QuestionOption o : options) {
                Map<String, Object> oMap = new LinkedHashMap<>();
                oMap.put("id", o.getId());
                oMap.put("optionText", o.getOptionText());
                oMap.put("orderIndex", o.getOrderIndex());
                optList.add(oMap);
            }
            qMap.put("options", optList);
            result.add(qMap);
        }
        return result;
    }

    @Override
    public boolean hasOpenAttempt(long testId, long userId) {
        List<TestAttempt> attempts = this.testAttemptRepo.getByUserAndTest(userId, testId);
        for (TestAttempt a : attempts) {
            if (a.getSubmittedAt() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> submit(long attemptId, List<Map<String, Object>> answersBody) {
        TestAttempt attempt = this.testAttemptRepo.getById(attemptId);
        if (attempt == null) {
            throw new IllegalArgumentException("Không tìm thấy lượt làm bài");
        }
        if (attempt.getSubmittedAt() != null) {
            throw new IllegalArgumentException("Bài làm đã được nộp trước đó");
        }

        Test test = attempt.getTestId();
        List<Question> activeQuestions = this.questionService.getActiveByTest(test.getId());

        Map<Long, Long> selectedByQuestion = new HashMap<>();
        if (answersBody != null) {
            for (Map<String, Object> ans : answersBody) {
                Object qIdObj = ans.get("questionId");
                Object optIdObj = ans.get("selectedOptionId");
                if (qIdObj == null) {
                    continue;
                }
                Long qId = Long.valueOf(String.valueOf(qIdObj));
                Long optId = optIdObj == null ? null : Long.valueOf(String.valueOf(optIdObj));
                selectedByQuestion.put(qId, optId);
            }
        }

        List<AttemptAnswer> answers = new ArrayList<>();
        int correctCount = 0;
        for (Question q : activeQuestions) {
            Long selectedOptionId = selectedByQuestion.get(q.getId());
            QuestionOption selectedOption = null;
            if (selectedOptionId != null) {
                selectedOption = this.questionOptionService.getById(selectedOptionId);
                if (selectedOption == null || !selectedOption.getQuestionId().getId().equals(q.getId())) {
                    throw new IllegalArgumentException("Đáp án không thuộc câu hỏi " + q.getId());
                }
            }
            boolean isCorrect = selectedOption != null && selectedOption.getIsCorrect();
            if (isCorrect) {
                correctCount++;
            }

            AttemptAnswer aa = new AttemptAnswer();
            aa.setAttemptId(attempt);
            aa.setQuestionId(q);
            aa.setSelectedOptionId(selectedOption);
            aa.setIsCorrect(isCorrect);
            answers.add(aa);
        }

        int total = activeQuestions.size();
        int score = total == 0 ? 0 : (correctCount * 100) / total;
        boolean passed = score >= test.getPassScore();

        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(new Date());
        attempt.setAttemptAnswerList(answers);
        this.testAttemptRepo.saveOrUpdate(attempt);

        Long userId = attempt.getUserId().getId();
        this.notificationService.create(userId, "Kết quả bài kiểm tra",
                "Bạn đạt " + score + " điểm cho bài \"" + test.getTitle() + "\" - " + (passed ? "ĐẠT" : "CHƯA ĐẠT"));
        if (passed) {
            boolean alreadyPassedBefore = false;
            for (TestAttempt prev : this.testAttemptRepo.getByUserAndTest(userId, test.getId())) {
                if (prev.getId().equals(attempt.getId())) {
                    continue;
                }
                if (prev.getPassed()) {
                    alreadyPassedBefore = true;
                    break;
                }
            }
            if (!alreadyPassedBefore) {
                this.pointTransactionService.awardPoints(userId, "TEST_PASSED", "Vượt qua bài kiểm tra: " + test.getTitle());
            }
            this.certificateService.checkAndIssue(userId, test.getCourseId().getId());
        }

        return buildAttemptDetail(attempt, answers);
    }

    @Override
    public Map<String, Object> getAttemptDetail(long attemptId) {
        TestAttempt attempt = this.testAttemptRepo.getById(attemptId);
        if (attempt == null) {
            return null;
        }
        List<AttemptAnswer> answers = this.testAttemptRepo.getAnswers(attemptId);
        return buildAttemptDetail(attempt, answers);
    }

    @Override
    public TestAttempt getById(long id) {
        return this.testAttemptRepo.getById(id);
    }

    @Override
    public List<TestAttempt> getMyAttempts(long userId, Long testId) {
        if (testId != null) {
            return this.testAttemptRepo.getByUserAndTest(userId, testId);
        }
        return this.testAttemptRepo.getByUser(userId);
    }

    @Override
    public List<TestAttempt> getByTest(long testId) {
        return this.testAttemptRepo.getByTest(testId);
    }
    
}
