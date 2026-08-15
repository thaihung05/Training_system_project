/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Course;
import com.tlh.pojo.ForumAnswer;
import com.tlh.pojo.ForumQuestion;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.ForumAnswerService;
import com.tlh.service.ForumQuestionService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure")
public class ApiForumController {
    @Autowired
    private ForumQuestionService forumQuestionService;

    @Autowired
    private ForumAnswerService forumAnswerService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }

    @PostMapping("/courses/{courseId}/forum/questions")
    public ResponseEntity<?> ask(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        User caller = currentUser(principal);
        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        if (!this.courseService.canView(caller, course)) {
            return new ResponseEntity<>("Bạn không có quyền đặt câu hỏi trong khoá học này", HttpStatus.FORBIDDEN);
        }
        ForumQuestion created = this.forumQuestionService.ask(courseId, caller, body.get("content"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/courses/{courseId}/forum/questions")
    public ResponseEntity<?> list(
            @PathVariable(value = "courseId") long courseId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        if (!this.courseService.canView(caller, course)) {
            return new ResponseEntity<>("Bạn không có quyền xem diễn đàn khoá học này", HttpStatus.FORBIDDEN);
        }
        List<Map<String, Object>> questions = this.forumQuestionService.getByCourse(courseId, page, size);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("canAnswer", this.courseService.canAnswerForum(caller, course));
        result.put("hasPendingQuestion", this.forumQuestionService.hasPendingQuestion(courseId, caller.getId()));
        result.put("questions", questions);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/forum/questions/{questionId}/answers")
    public ResponseEntity<?> answer(
            @PathVariable(value = "questionId") long questionId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        User caller = currentUser(principal);
        ForumQuestion question = this.forumQuestionService.getById(questionId);
        if (question == null) {
            return new ResponseEntity<>("Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND);
        }
        boolean isAsker = question.getUserId().getId().equals(caller.getId());
        if (!isAsker && !this.courseService.canAnswerForum(caller, question.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền trả lời câu hỏi này", HttpStatus.FORBIDDEN);
        }
        ForumAnswer created = this.forumAnswerService.answer(questionId, caller, body.get("content"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
