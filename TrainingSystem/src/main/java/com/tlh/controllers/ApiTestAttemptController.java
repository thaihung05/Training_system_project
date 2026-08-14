/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Test;
import com.tlh.pojo.TestAttempt;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.TestAttemptService;
import com.tlh.service.TestService;
import com.tlh.service.UserService;
import java.security.Principal;
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
public class ApiTestAttemptController {
    @Autowired
    private TestAttemptService testAttemptService;

    @Autowired
    private TestService testService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @PostMapping("/tests/{testId}/attempts/start")
    public ResponseEntity<?> startAttempt(
            @PathVariable(value = "testId") long testId, 
            Principal principal) {
        User caller = currentUser(principal);
        TestAttempt created = this.testAttemptService.startAttempt(testId, caller);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/tests/{testId}/questions-for-attempt")
    public ResponseEntity<?> getQuestionsForAttempt(
            @PathVariable(value = "testId") long testId, 
            Principal principal) {
        User caller = currentUser(principal);
        if (!this.testAttemptService.hasOpenAttempt(testId, caller.getId())) {
            return new ResponseEntity<>("Bạn chưa có lượt làm bài đang mở cho bài kiểm tra này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.testAttemptService.getQuestionsForAttempt(testId), HttpStatus.OK);
    }
    
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<?> submit(
            @PathVariable(value = "attemptId") long attemptId,
            @RequestBody Map<String, List<Map<String, Object>>> body,
            Principal principal) {
        User caller = currentUser(principal);
        List<Map<String, Object>> answers = body.get("answers");
        Map<String, Object> result = this.testAttemptService.submit(attemptId, answers, caller);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/attempts/{attemptId}/abandon")
    public ResponseEntity<?> abandon(
            @PathVariable(value = "attemptId") long attemptId,
            Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.testAttemptService.abandon(attemptId, caller), HttpStatus.OK);
    }
    
    @GetMapping("/attempts/{id}")
    public ResponseEntity<?> getAttempt(
            @PathVariable(value = "id") long id, 
            Principal principal) {
        TestAttempt existed = this.testAttemptService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy lượt làm bài", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        boolean isOwner = existed.getUserId().getId().equals(caller.getId());
        boolean canManage = this.courseService.canManage(caller, existed.getTestId().getCourseId());
        if (!isOwner && !canManage) {
            return new ResponseEntity<>("Bạn không có quyền xem lượt làm bài này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.testAttemptService.getAttemptDetail(id), HttpStatus.OK);
    }
    
    @GetMapping("/attempts/my")
    public ResponseEntity<?> myAttempts(
            @RequestParam(value = "testId", required = false) Long testId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.testAttemptService.getMyAttempts(caller.getId(), testId, page, size), HttpStatus.OK);
    }

    @GetMapping("/tests/{testId}/attempts")
    public ResponseEntity<?> attemptsOfTest(
            @PathVariable(value = "testId") long testId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        Test t = this.testService.getById(testId);
        if (t == null) {
            return new ResponseEntity<>("Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, t.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền xem danh sách này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.testAttemptService.getByTest(testId, page, size), HttpStatus.OK);
    }
}
