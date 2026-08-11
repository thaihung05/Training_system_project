/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Course;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.List;
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

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure/courses")
public class ApiSecureCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(
            @RequestParam(value = "kw", required = false) String kw,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "activeOnly", required = false) Boolean activeOnly,
            Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.courseService.getCourses(kw, departmentId, caller, page, size, activeOnly), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable(value = "id") long id, Principal principal) {
        Course c = this.courseService.getCourseById(id);
        if (c == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canView(caller, c)) {
            return new ResponseEntity<>("Bạn không có quyền xem khoá học này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyCourses(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        if (caller == null || !("TRAINER".equals(caller.getRole()) || "ADMIN".equals(caller.getRole()))) {
            return new ResponseEntity<>("Bạn không có quyền xem danh sách này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.courseService.getMyCourses(caller, page, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course c, Principal principal) {
        User caller = currentUser(principal);
        if (caller == null || !("TRAINER".equals(caller.getRole()) || "ADMIN".equals(caller.getRole()))) {
            return new ResponseEntity<>("Bạn không có quyền tạo khoá học", HttpStatus.FORBIDDEN);
        }
        c.setId(null);
        c.setCreatedBy(caller);
        Course created = this.courseService.addOrUpdate(c);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable(value = "id") long id, @RequestBody Course c, Principal principal) {
        Course existed = this.courseService.getCourseById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!courseService.canManage(caller, existed)) {
            return new ResponseEntity<>("Bạn không có quyền chỉnh sửa khóa học này", HttpStatus.FORBIDDEN);
        }
        c.setId(id);
        c.setCreatedBy(existed.getCreatedBy());
        Course updated = this.courseService.addOrUpdate(c);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateCourse(@PathVariable(value = "id") long id, Principal principal) {
        Course existed = this.courseService.getCourseById(id);
        if (existed == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!courseService.canManage(caller, existed)) {
            return new ResponseEntity<>("Bạn không có quyền chỉnh sửa khóa học này", HttpStatus.FORBIDDEN);
        }
        this.courseService.deactivateCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
