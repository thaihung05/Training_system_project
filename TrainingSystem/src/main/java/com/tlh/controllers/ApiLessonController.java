/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Course;
import com.tlh.pojo.Lesson;
import com.tlh.pojo.User;
import com.tlh.service.CourseService;
import com.tlh.service.LessonService;
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
public class ApiLessonController {
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal){
        return this.userService.getUserByUsername(principal.getName());
    }

    private boolean canView(User caller, Course course){
        return this.courseService.canView(caller, course);
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<?> getLessonsByCourse(@PathVariable(value = "courseId") long courseId, Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c == null)
            return new ResponseEntity<>("Không tìm thấy khoá học tương ứng", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!canView(caller, c))
            return new ResponseEntity<>("Bạn chưa được ghi danh khoá học này", HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(this.lessonService.getLessonByCourse(courseId),HttpStatus.OK);
    }
    
    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<?> addLesson(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Lesson l, 
            Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c==null)
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c))
            return new ResponseEntity<>("Bạn không có quyền thêm bài học vào khoá học này", HttpStatus.FORBIDDEN);
        l.setId(null);
        l.setCourseId(c);
        Lesson created = this.lessonService.addLesson(l);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    } 
    
    @GetMapping("/lessons/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable(value = "id") long id, Principal principal) {
        Lesson l = this.lessonService.getLessonById(id);
        if (l == null) {
            return new ResponseEntity<>("Không tìm thấy bài học", HttpStatus.NOT_FOUND);
        }
        User caller = currentUser(principal);
        if (!canView(caller, l.getCourseId()))
            return new ResponseEntity<>("Bạn chưa được ghi danh khoá học này", HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }
    
    @PutMapping("/lessons/{id}")
    public ResponseEntity<?> updateLesson(
            @PathVariable(value = "id") long id,
            @RequestBody Lesson l,
            Principal principal){
        Lesson existed = this.lessonService.getLessonById(id);
        if (existed == null)
            return new ResponseEntity<>("Không tìm thấy bài học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, existed.getCourseId()))
            return new ResponseEntity<>("Bạn không quyền chỉnh sửa bài học này", HttpStatus.FORBIDDEN);
        l.setId(id);
        l.setCourseId(existed.getCourseId());
        l.setOrderIndex(existed.getOrderIndex());
        Lesson updated = this.lessonService.updateLesson(l);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable(value = "id") long id, Principal principal){
        Lesson l = this.lessonService.getLessonById(id);
        if (l == null)
            return new ResponseEntity<>("Không tìm thấy bài học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, l.getCourseId()))
            return new ResponseEntity<>("Bạn không có quyền xoá bài học này", HttpStatus.FORBIDDEN);
        this.lessonService.deleteLesson(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("/courses/{courseId}/lessons/reorder")
    public ResponseEntity<?> reorder(
            @PathVariable(value = "courseId") long courseId,
            @RequestBody Map<String, List<Long>> body, 
            Principal principal){
        Course c = this.courseService.getCourseById(courseId);
        if (c == null)
            return new ResponseEntity<>("Không tìm thấy khoá học", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!this.courseService.canManage(caller, c))
            return new ResponseEntity<>("Bạn không có quyền sắp xếp bài học của khoá học này", HttpStatus.FORBIDDEN);
        List<Long> orderedLessonIds = body.get("orderedLessonIds");
        if (orderedLessonIds == null){
            return new ResponseEntity<>("Thiếu orderedLessonIds", HttpStatus.BAD_REQUEST);
        }
        List<Lesson> reordered = this.lessonService.reorder(courseId, orderedLessonIds);
        return new ResponseEntity<>(reordered, HttpStatus.OK);
    }
    
}
