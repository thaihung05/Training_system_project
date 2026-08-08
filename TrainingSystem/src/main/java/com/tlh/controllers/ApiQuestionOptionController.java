/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Question;
import com.tlh.pojo.QuestionOption;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.QuestionOptionService;
import com.tlh.service.QuestionService;
import com.tlh.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure")
public class ApiQuestionOptionController {

    @Autowired
    private QuestionOptionService questionOptionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<?> addOption(
            @PathVariable(value = "questionId") long questionId,
            @RequestBody QuestionOption body,
            Principal principal) {
        Question q = this.questionService.getById(questionId);
        if (q == null) {
            return new ResponseEntity<>("Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, q.getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền thêm đáp án cho câu hỏi này", HttpStatus.FORBIDDEN);
        }
        body.setId(null);
        body.setQuestionId(q);
        QuestionOption created = this.questionOptionService.addOption(body);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/question-options/{id}")
    public ResponseEntity<?> updateOption(
            @PathVariable(value = "id") long id,
            @RequestBody QuestionOption body,
            Principal principal) {
        QuestionOption existed = this.questionOptionService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy đáp án", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getQuestionId().getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền chỉnh sửa đáp án này", HttpStatus.FORBIDDEN);
        }
        body.setId(id);
        body.setQuestionId(existed.getQuestionId());
        body.setIsCorrect(existed.getIsCorrect());
        QuestionOption updated = this.questionOptionService.updateOption(body);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/question-options/{id}")
    public ResponseEntity<?> deleteOption(@PathVariable(value = "id") long id, Principal principal) {
        QuestionOption existed = this.questionOptionService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy đáp án", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getQuestionId().getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền xoá đáp án này", HttpStatus.FORBIDDEN);
        }
        this.questionOptionService.deleteOption(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/questions/{id}/correct-option/{optionId}")
    public ResponseEntity<?> setCorrectOption(
            @PathVariable(value = "id") long id,
            @PathVariable(value = "optionId") long optionId,
            Principal principal) {
        Question q = this.questionService.getById(id);
        if (q == null) {
            return new ResponseEntity<>("Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, q.getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền với câu hỏi này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.questionOptionService.setCorrectOption(id, optionId), HttpStatus.OK);
    }
}
