/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Question;
import com.tlh.pojo.Test;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.QuestionService;
import com.tlh.service.TestService;
import com.tlh.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure")
public class ApiQuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestService testService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }

    @PostMapping("/tests/{testId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable(value = "testId") long testId,
            @RequestBody Question body,
            Principal principal) {
        Test t = this.testService.getById(testId);
        if (t == null) {
            return new ResponseEntity<>("Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, t.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền thêm câu hỏi vào bài kiểm tra này", HttpStatus.FORBIDDEN);
        }
        body.setId(null);
        body.setTestId(t);
        Question created = this.questionService.addQuestion(body);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable(value = "id") long id,
            @RequestBody Question body,
            Principal principal) {
        Question existed = this.questionService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền chỉnh sửa câu hỏi này", HttpStatus.FORBIDDEN);
        }
        body.setId(id);
        body.setTestId(existed.getTestId());
        Question updated = this.questionService.updateQuestion(body);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable(value = "id") long id, Principal principal) {
        Question existed = this.questionService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getTestId().getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền xoá câu hỏi này", HttpStatus.FORBIDDEN);
        }
        this.questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/tests/{testId}/questions/bulk-import")
    public ResponseEntity<?> bulkImportQuestions(
            @PathVariable(value = "testId") long testId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        Test t = this.testService.getById(testId);
        if (t == null) {
            return new ResponseEntity<>("Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, t.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền thêm câu hỏi vào bài kiểm tra này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.questionService.bulkImportQuestions(t, file), HttpStatus.OK);
    }

    @GetMapping("/tests/{id}/questions")
    public ResponseEntity<?> getQuestionsForCompose(
            @PathVariable(value = "id") long id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        Test t = this.testService.getById(id);
        if (t == null) {
            return new ResponseEntity<>("Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, t.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền xem câu hỏi của bài kiểm tra này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.questionService.getQuestionsForCompose(id, page, size), HttpStatus.OK);
    }
}
