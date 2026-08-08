/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Certificate;
import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.Test;
import com.tlh.pojo.TestAttempt;
import com.tlh.pojo.User;
import com.tlh.repository.CertificateRepository;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.repository.TestRepository;
import com.tlh.service.CertificateService;
import com.tlh.service.CourseService;
import com.tlh.service.NotificationService;
import com.tlh.service.PointTransactionService;
import com.tlh.service.UserBadgeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class CertificateServiceImpl implements CertificateService{

    @Autowired
    private CertificateRepository certificateRepo;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private TestRepository testRepo;

    @Autowired
    private TestAttemptRepository testAttemptRepo;

    @Autowired
    private CourseService courseService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private UserBadgeService userBadgeService;
    
    @Override
    public List<Certificate> getMyCertificates(long userId) {
        return this.certificateRepo.getByUser(userId);
    }

    @Override
    public Certificate getById(long id) {
        return this.certificateRepo.getById(id);
    }

    @Override
    public List<Certificate> getByCourse(long courseId) {
        return this.certificateRepo.getByCourse(courseId);
    }

    @Override
    public Certificate updatePdfUrl(long id, String pdfUrl) {
        if (pdfUrl == null || pdfUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Link PDF không được để trống");
        }
        if (pdfUrl.trim().length() > 500) {
            throw new IllegalArgumentException("Link PDF tối đa 500 ký tự");
        }
        Certificate c = this.certificateRepo.getById(id);
        if (c == null) {
            throw new IllegalArgumentException("Không tìm thấy chứng chỉ");
        }
        c.setPdfUrl(pdfUrl.trim());
        this.certificateRepo.saveOrUpdate(c);
        return c;
    }

    @Override
    public Certificate checkAndIssue(Long userId, Long courseId) {
        try {
            if (userId == null || courseId == null) {
                return null;
            }
            Certificate existed = this.certificateRepo.getByUserAndCourse(userId, courseId);
            if (existed != null) {
                return existed;
            }

            Enrollment enrollment = this.enrollmentRepo.getByCourseAndUser(courseId, userId);
            if (enrollment == null || enrollment.getCompletedAt() == null) {
                return null;
            }

            List<Test> tests = this.testRepo.getByCourse(courseId);
            for (Test t : tests) {
                if (!t.getIsActive()) {
                    continue;
                }
                List<TestAttempt> attempts = this.testAttemptRepo.getByUserAndTest(userId, t.getId());
                boolean passedAny = false;
                for (TestAttempt a : attempts) {
                    if (a.getPassed()) {
                        passedAny = true;
                        break;
                    }
                }
                if (!passedAny) {
                    return null;
                }
            }

            Certificate c = new Certificate();
            c.setUserId(new User(userId));
            c.setCourseId(new Course(courseId));
            c.setCertificateCode("CERT-" + courseId + "-" + userId + "-" + System.currentTimeMillis());
            this.certificateRepo.saveOrUpdate(c);

            Course course = this.courseService.getCourseById(courseId);
            String courseTitle = course != null ? course.getTitle() : "";
            this.notificationService.create(userId, "Chứng chỉ mới",
                    "Bạn đã hoàn thành khoá học " + courseTitle + " và nhận được chứng chỉ");
            this.pointTransactionService.awardPoints(userId, "COURSE_COMPLETED", "Hoàn thành khoá học: " + courseTitle);

            long total = this.certificateRepo.countByUser(userId);
            if (total == 1) {
                this.userBadgeService.checkAndAward(userId, "CERT_1");
            }
            if (total == 5) {
                this.userBadgeService.checkAndAward(userId, "CERT_5");
            }
            if (total == 10) {
                this.userBadgeService.checkAndAward(userId, "CERT_10");
            }

            return c;
        } catch (Exception e) {
            System.err.println("CertificateService.checkAndIssue that bai: " + e.getMessage());
            return null;
        }
    }
    
}
