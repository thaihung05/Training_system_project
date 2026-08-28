/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.LessonProgress;
import com.tlh.pojo.User;
import com.tlh.service.LessonProgressService;
import com.tlh.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure/lesson-progress")
public class ApiLessonProgressController {
    @Autowired
    private LessonProgressService lessonProgressService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable(value = "id") long id, Principal principal){
        LessonProgress lp = this.lessonProgressService.getById(id);
        if (lp == null)
            return new ResponseEntity<>("Không tìm thấy tiến độ bài học", HttpStatus.NOT_FOUND);

        User caller = currentUser(principal);
        if (!lp.getEnrollmentId().getUserId().getId().equals(caller.getId()))
            return new ResponseEntity<>("Bạn không có quyền với tiến độ này", HttpStatus.FORBIDDEN);
        LessonProgress updated = this.lessonProgressService.markComplete(id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
