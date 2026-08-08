/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Course;
import com.tlh.pojo.Test;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.TestService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ApiTestController {

    @Autowired
    private TestService testService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }

    @PostMapping("/courses/{courseId}/tests")
    public ResponseEntity<?> createTest(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Test body,
            Principal principal) {
        Course c = this.courseService.getCourseById(courseId);
        if (c == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c)) {
            return new ResponseEntity<>("Bạn không có quyền tạo bài kiểm tra cho khoá học này", HttpStatus.FORBIDDEN);
        }
        body.setId(null);
        body.setCourseId(c);
        Test created = this.testService.addTest(body);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/tests/{id}")
    public ResponseEntity<?> updateTest(
            @PathVariable(value = "id") long id,
            @RequestBody Test body,
            Principal principal) {
        Test existed = this.testService.getById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getCourseId())) {
            return new ResponseEntity<>("Bạn không có quyền chỉnh sửa bài kiểm tra này", HttpStatus.FORBIDDEN);
        }
        body.setId(id);
        body.setCourseId(existed.getCourseId());
        Test updated = this.testService.updateTest(body);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @GetMapping("/courses/{courseId}/tests")
    public ResponseEntity<?> getTests(@PathVariable(value = "courseId") long courseId, Principal principal) {
        Course c = this.courseService.getCourseById(courseId);
        if (c == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (this.courseService.canManage(caller, c)) {
            List<Test> tests = this.testService.getByCourse(courseId);
            return new ResponseEntity<>(tests, HttpStatus.OK);
        }
        if (!this.courseService.canView(caller, c)) {
            return new ResponseEntity<>("Bạn không có quyền xem bài kiểm tra của khoá học này", HttpStatus.FORBIDDEN);
        }
        List<Test> tests = this.testService.getByCourse(courseId);
        List<Test> activeOnly = new ArrayList<>();
        for (Test t : tests) {
            if (t.getIsActive()) {
                activeOnly.add(t);
            }
        }
        return new ResponseEntity<>(activeOnly, HttpStatus.OK);
    }
}
