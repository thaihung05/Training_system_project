/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.ForumAnswer;
import com.tlh.pojo.ForumQuestion;
import com.tlh.pojo.User;
import com.tlh.repository.ForumAnswerRepository;
import com.tlh.repository.ForumQuestionRepository;
import com.tlh.service.CourseService;
import com.tlh.service.ForumQuestionService;
import com.tlh.service.NotificationService;
import com.tlh.service.UserService;
import java.util.ArrayList;
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
public class ForumQuestionServiceImpl implements ForumQuestionService{

    @Autowired
    private ForumQuestionRepository forumQuestionRepo;

    @Autowired
    private ForumAnswerRepository forumAnswerRepo;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public ForumQuestion ask(long courseId, User caller, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Câu hỏi không được để trống");
        }
        if (content.trim().length() > 65535) {
            throw new IllegalArgumentException("Câu hỏi quá dài");
        }
        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Không tìm thấy khoá học");
        }
        ForumQuestion q = new ForumQuestion();
        q.setCourseId(course);
        q.setUserId(caller);
        q.setContent(content.trim());
        this.forumQuestionRepo.saveOrUpdate(q);

        notifyTrainersOfNewQuestion(course, caller);
        return q;
    }

    private void notifyTrainersOfNewQuestion(Course course, User asker) {
        String link = "/courses/" + course.getId() + "?tab=forum";
        String content = asker.getName() + " vừa đặt câu hỏi trong diễn đàn khoá học " + course.getTitle();

        if (course.getDepartmentId() == null) {
            for (User t : this.userService.getUsersByRole("TRAINER")) {
                if (t.getIsActive() && !t.getId().equals(asker.getId())) {
                    this.notificationService.create(t.getId(), "Có câu hỏi mới trong diễn đàn", content, link);
                }
            }
            return;
        }

        if (course.getCreatedBy() != null && !course.getCreatedBy().getId().equals(asker.getId())) {
            this.notificationService.create(course.getCreatedBy().getId(), "Có câu hỏi mới trong diễn đàn", content, link);
        }
    }

    @Override
    public List<Map<String, Object>> getByCourse(long courseId, Integer page, Integer size) {
        List<ForumQuestion> questions = this.forumQuestionRepo.getByCourse(courseId, page, size);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumQuestion q : questions) {
            List<ForumAnswer> answers = this.forumAnswerRepo.getByQuestion(q.getId());
            List<Map<String, Object>> answerList = new ArrayList<>();
            for (ForumAnswer a : answers) {
                Map<String, Object> aMap = new LinkedHashMap<>();
                aMap.put("id", a.getId());
                aMap.put("content", a.getContent());
                aMap.put("createdAt", a.getCreatedAt());
                aMap.put("userId", a.getUserId());
                answerList.add(aMap);
            }

            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("content", q.getContent());
            qMap.put("createdAt", q.getCreatedAt());
            qMap.put("userId", q.getUserId());
            qMap.put("answers", answerList);
            result.add(qMap);
        }
        return result;
    }

    @Override
    public ForumQuestion getById(long id) {
        return this.forumQuestionRepo.getById(id);
    }

}
