/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.EnrollmentService;
import com.tlh.service.LessonProgressService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ApiEnrollmentController {
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LessonProgressService lessonProgressService;
    
    private User currentUser(Principal principal){
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @PostMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<?> enroll(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Map<String, List<Long>> body,
            Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c==null)
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c))
            return new ResponseEntity<>("Bạn không có quyền ghi danh cho khoá học này", HttpStatus.FORBIDDEN);
        if (!c.getIsActive()) 
            return new ResponseEntity<>("Khoá học chưa được kích hoạt", HttpStatus.BAD_REQUEST);
        List<Long> userIds = body.get("userIds");
        if (userIds == null || userIds.isEmpty())
            return new ResponseEntity<>("Danh sách nhân viên không được rỗng", HttpStatus.BAD_REQUEST);
        Map<String, Object> result = this.enrollmentService.enrollUsers(c, userIds);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @PostMapping("/courses/{courseId}/enrollments/by-store")
    public ResponseEntity<?> enrollByStore(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Map<String, Long> body,
            Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c==null)
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c))
            return new ResponseEntity<>("Bạn không có quyền ghi danh cho khoá học này", HttpStatus.FORBIDDEN);
        if (!c.getIsActive())
            return new ResponseEntity<>("Khoá học chưa được kích hoạt", HttpStatus.BAD_REQUEST);
        Long storeId = body.get("storeId");
        if (storeId == null)
            return new ResponseEntity<>("Thiếu storeId", HttpStatus.BAD_REQUEST);
        Map<String, Object> result = this.enrollmentService.enrollStore(c, storeId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<?> roster(
            @PathVariable(value = "courseId") long courseId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c==null)
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c))
            return new ResponseEntity<>("Bạn không có quyền xem danh sách ghi danh này", HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(this.enrollmentService.getRoster(courseId, page, size), HttpStatus.OK);
    }

    @GetMapping("/enrollments/my")
    public ResponseEntity<?> myEnrollments(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal){
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.enrollmentService.getMyEnrollments(caller.getId(), page, size), HttpStatus.OK);
    }
    
    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<?> unenroll(@PathVariable(value = "id") long id, Principal principal){
        Enrollment e = this.enrollmentService.getById(id);
        if (e==null)
            return new ResponseEntity<>("Không tìm thấy lượt ghi danh", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, e.getCourseId())){
            return new ResponseEntity<>("Bạn không có quyền huỷ ghi danh này", HttpStatus.FORBIDDEN);
        }
        this.enrollmentService.unenroll(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/enrollments/{id}/progress")
    public ResponseEntity<?> progress(@PathVariable(value = "id") long id, Principal principal){
        Enrollment e = this.enrollmentService.getById(id);
        if (e==null)
            return new ResponseEntity<>("Không tìm thấy lượt ghi danh", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        boolean isOwner = e.getUserId().getId().equals(caller.getId());
        boolean canManage = this.courseService.canManage(caller, e.getCourseId());
        if (!isOwner && !canManage) 
            return new ResponseEntity<>("Bạn không có quyền xem tiến độ này", HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(this.lessonProgressService.getByEnrollment(id), HttpStatus.OK);
    }
}
