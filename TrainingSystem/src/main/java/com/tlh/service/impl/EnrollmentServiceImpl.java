/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.Lesson;
import com.tlh.pojo.LessonProgress;
import com.tlh.pojo.User;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.LessonProgressRepository;
import com.tlh.service.CertificateService;
import com.tlh.service.EnrollmentService;
import com.tlh.service.LessonService;
import com.tlh.service.NotificationService;
import com.tlh.service.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class EnrollmentServiceImpl implements EnrollmentService{
    
    @Autowired
    private EnrollmentRepository enrollmentRepo;
    
    @Autowired
    private LessonProgressRepository lessonProgressRepo;
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CertificateService certificateService;

    @Override
    public Map<String, Object> enrollUsers(Course course, List<Long> userIds) {
        List<Enrollment> enrolled = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();
        List<Lesson> lessons = this.lessonService.getLessonByCourse(course.getId());
        
        for (Long userId : userIds){
            User u = this.userService.getUserById(userId);
            if (u==null || !u.getIsActive()){
                skipped.add(userId);
                continue;
            }
            Enrollment existed = this.enrollmentRepo.getByCourseAndUser(course.getId(), userId);
            if (existed != null){
                skipped.add(userId);
                continue;
            }
            Enrollment e = new Enrollment();
            e.setCourseId(course);
            e.setUserId(u);
            e.setProgressPercent(0);
            this.enrollmentRepo.saveOrUpdate(e);
            
            for (Lesson l : lessons) {
                LessonProgress lp = new LessonProgress();
                lp.setEnrollmentId(e);
                lp.setLessonId(l);
                lp.setIsCompleted(false);
                this.lessonProgressRepo.saveOrUpdate(lp);
            }
            
            this.notificationService.create(userId, "Ghi danh khoá học", "Bạn được ghi danh khoá học "+course.getTitle());
            enrolled.add(e);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("enrolled", enrolled);
        result.put("skipped", skipped);
        return result;
    }

    @Override
    public Map<String, Object> enrollDepartment(Course course, long departmentId) {
        List<User> deptUsers = this.userService.getUsersByDepartment(String.valueOf(departmentId));
        List<Long> employeeIds = new ArrayList<>();
        for (User u : deptUsers) {
            if ("EMPLOYEE".equals(u.getRole())) {
                employeeIds.add(u.getId());
            }
        }
        return this.enrollUsers(course, employeeIds);
    }

    @Override
    public List<Enrollment> getRoster(long courseId) {
        return this.enrollmentRepo.getByCourse(courseId);
    }

    @Override
    public List<Enrollment> getMyEnrollments(long userId) {
        return this.enrollmentRepo.getByUser(userId);
    }

    @Override
    public Enrollment getById(long id) {
        return this.enrollmentRepo.getById(id);
    }

    @Override
    public void unenroll(long id) {
        this.enrollmentRepo.delete(id);
    }

    @Override
    public boolean isEnrolled(long courseId, long userId) {
        return this.enrollmentRepo.getByCourseAndUser(courseId, userId) != null;
    }

    @Override
    public void recalcProgress(long enrollmentId) {
        Enrollment e = this.enrollmentRepo.getById(enrollmentId);
        if (e == null)
            return;
        List<LessonProgress> all = this.lessonProgressRepo.getByEnrollment(enrollmentId);
        int total = all.size();
        int completedCount = 0;
        for (LessonProgress lp : all){
            if (lp.getIsCompleted()) {
                completedCount++;
            }
        }
        int percent = total == 0 ? 0 : (completedCount * 100) / total;
        e.setProgressPercent(percent);
        boolean justCompleted = percent == 100 && e.getCompletedAt() == null;
        if (justCompleted) {
            e.setCompletedAt(new Date());
        }
        this.enrollmentRepo.saveOrUpdate(e);
        if (justCompleted) {
            this.certificateService.checkAndIssue(e.getUserId().getId(), e.getCourseId().getId());
        }
    }
    
}
