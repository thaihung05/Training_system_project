/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Chain;
import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.Lesson;
import com.tlh.pojo.LessonProgress;
import com.tlh.pojo.Region;
import com.tlh.pojo.Store;
import com.tlh.pojo.Test;
import com.tlh.pojo.TestAttempt;
import com.tlh.pojo.User;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.CourseRepository;
import com.tlh.repository.LessonProgressRepository;
import com.tlh.repository.TestAttemptRepository;
import com.tlh.repository.TestRepository;
import com.tlh.service.CertificateService;
import com.tlh.service.CourseService;
import com.tlh.service.EnrollmentService;
import com.tlh.service.LessonService;
import com.tlh.service.NotificationService;
import com.tlh.service.StoreService;
import com.tlh.service.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService{
    
    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private CourseRepository courseRepo;
    
    @Autowired
    private LessonProgressRepository lessonProgressRepo;
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestRepository testRepo;

    @Autowired
    private TestAttemptRepository testAttemptRepo;

    @Override
    public Map<String, Object> enrollUsers(Course course, List<Long> userIds) {
        if (course == null || course.getId() == null) {
            throw new IllegalArgumentException("Không tìm thấy khóa học");
        }
        course = this.courseRepo.getCourseByIdForUpdate(course.getId());
        if (course == null || !course.getIsActive()) {
            throw new IllegalArgumentException("Khóa học chưa được mở, không thể ghi danh");
        }
        this.courseService.validateReadyForEnrollment(course.getId());
        List<Enrollment> enrolled = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();
        List<Lesson> lessons = this.lessonService.getLessonByCourse(course.getId());
        
        for (Long userId : userIds){
            if (userId == null) {
                skipped.add(userId);
                continue;
            }
            User u = this.userService.getUserById(userId);
            if (!this.canEnroll(u, course)){
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
            
            this.notificationService.create(userId, "Ghi danh khoá học", "Bạn được ghi danh khoá học "+course.getTitle(), "/courses/" + course.getId());
            enrolled.add(e);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("enrolled", enrolled);
        result.put("skipped", skipped);
        return result;
    }

    @Override
    public Map<String, Object> enrollStore(Course course, long storeId) {
        Store store = this.storeService.getStoreById(storeId);
        if (store == null) {
            throw new IllegalArgumentException("Siêu thị không tồn tại");
        }
        if (!this.isStoreInCourseScope(store, course)) {
            throw new IllegalArgumentException("Siêu thị không thuộc phạm vi khóa học");
        }

        List<User> storeUsers = this.userService.getUsersByStore(String.valueOf(storeId));
        List<Long> employeeIds = new ArrayList<>();
        for (User u : storeUsers) {
            if ("EMPLOYEE".equals(u.getRole())) {
                employeeIds.add(u.getId());
            }
        }
        return this.enrollUsers(course, employeeIds);
    }

    private boolean canEnroll(User user, Course course) {
        return user != null
                && "EMPLOYEE".equals(user.getRole())
                && user.getIsActive()
                && this.isStoreInCourseScope(user.getStoreId(), course);
    }

    private boolean isStoreInCourseScope(Store store, Course course) {
        if (store == null || course == null
                || store.getChainId() == null || store.getRegionId() == null) {
            return false;
        }

        List<Chain> chains = course.getChains();
        if (chains != null && !chains.isEmpty()) {
            boolean chainMatch = false;
            for (Chain chain : chains) {
                if (chain.getId().equals(store.getChainId().getId())) {
                    chainMatch = true;
                    break;
                }
            }
            if (!chainMatch) {
                return false;
            }
        }

        List<Region> regions = course.getRegions();
        if (regions != null && !regions.isEmpty()) {
            for (Region region : regions) {
                if (region.getId().equals(store.getRegionId().getId())) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    @Override
    public List<Enrollment> getRoster(long courseId, Integer page, Integer size) {
        return this.enrollmentRepo.getByCourse(courseId, page, size);
    }

    @Override
    public List<Enrollment> getMyEnrollments(long userId, Integer page, Integer size) {
        return this.enrollmentRepo.getByUser(userId, page, size);
    }

    @Override
    public Enrollment getById(long id) {
        return this.enrollmentRepo.getById(id);
    }

    @Override
    public void unenroll(long id) {
        Enrollment enrollment = this.enrollmentRepo.getById(id);
        if (enrollment == null) {
            return;
        }
        if (enrollment.getCompletedAt() != null) {
            throw new IllegalArgumentException("Không thể hủy ghi danh vì nhân viên đã hoàn thành khóa học");
        }
        for (LessonProgress progress : this.lessonProgressRepo.getByEnrollment(id)) {
            if (progress.getIsCompleted()) {
                throw new IllegalArgumentException("Không thể hủy ghi danh vì nhân viên đã bắt đầu học");
            }
        }
        for (Test test : this.testRepo.getByCourse(enrollment.getCourseId().getId())) {
            if (!this.testAttemptRepo.getByUserAndTest(enrollment.getUserId().getId(), test.getId()).isEmpty()) {
                throw new IllegalArgumentException("Không thể hủy ghi danh vì nhân viên đã có lượt làm bài");
            }
        }
        this.notificationService.create(
                enrollment.getUserId().getId(),
                "Hủy ghi danh",
                "Bạn đã được hủy khỏi khóa học " + enrollment.getCourseId().getTitle(),
                "/my-courses");
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
        int totalUnits = all.size();
        int completedUnits = 0;
        for (LessonProgress lp : all){
            if (lp.getIsCompleted()) {
                completedUnits++;
            }
        }

        List<Test> tests = this.testRepo.getByCourse(e.getCourseId().getId());
        for (Test test : tests) {
            if (!test.getIsActive()) {
                continue;
            }
            totalUnits++;
            List<TestAttempt> attempts = this.testAttemptRepo.getByUserAndTest(e.getUserId().getId(), test.getId());
            boolean passed = false;
            for (TestAttempt attempt : attempts) {
                if (attempt.getPassed()) {
                    passed = true;
                    break;
                }
            }
            if (passed) {
                completedUnits++;
            }
        }

        int percent = totalUnits == 0 ? 0 : (completedUnits * 100) / totalUnits;
        e.setProgressPercent(percent);
        boolean justCompleted = totalUnits > 0 && percent == 100 && e.getCompletedAt() == null;
        if (justCompleted) {
            e.setCompletedAt(new Date());
        } else if (percent < 100 && e.getCompletedAt() != null) {
            e.setCompletedAt(null);
        }
        this.enrollmentRepo.saveOrUpdate(e);
        if (justCompleted) {
            this.certificateService.checkAndIssue(e.getUserId().getId(), e.getCourseId().getId());
        }
    }
    
}
