/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Certificate;
import com.tlh.pojo.Course;
import com.tlh.pojo.User;
import com.tlh.service.CertificateService;
import com.tlh.service.CourseService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/secure")
public class ApiCertificateController {
    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @GetMapping("/certificates/my")
    public ResponseEntity<?> myCertificates(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.certificateService.getMyCertificates(caller.getId(), page, size), HttpStatus.OK);
    }
    
    @GetMapping("/certificates/{id}")
    public ResponseEntity<?> getCertificate(
            @PathVariable(value = "id") long id, 
            Principal principal) {
        Certificate c = this.certificateService.getById(id);
        if (c == null) {
            return new ResponseEntity<>("Không tìm thấy chứng chỉ", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        boolean isOwner = c.getUserId().getId().equals(caller.getId());
        boolean canManage = this.courseService.canManage(caller, c.getCourseId());
        if (!isOwner && !canManage) {
            return new ResponseEntity<>("Bạn không có quyền xem chứng chỉ này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }
    
    @GetMapping("/courses/{courseId}/certificates")
    public ResponseEntity<?> getCertificatesOfCourse(
            @PathVariable(value = "courseId") long courseId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, course)) {
            return new ResponseEntity<>("Bạn không có quyền xem danh sách này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.certificateService.getByCourse(courseId, page, size), HttpStatus.OK);
    }
    
    @PutMapping("/certificates/{id}/pdf-url")
    public ResponseEntity<?> updatePdfUrl(
            @PathVariable(value = "id") long id, 
            @RequestBody Map<String, String> body) {
        Certificate updated = this.certificateService.updatePdfUrl(id, body.get("pdfUrl"));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
