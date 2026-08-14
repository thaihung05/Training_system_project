/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Chain;
import com.tlh.pojo.Course;
import com.tlh.pojo.Region;
import com.tlh.pojo.Store;
import com.tlh.pojo.User;
import com.tlh.repository.CourseRepository;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.LessonRepository;
import com.tlh.service.ChainService;
import com.tlh.service.CourseService;
import com.tlh.service.EnrollmentService;
import com.tlh.service.NotificationService;
import com.tlh.service.RegionService;
import com.tlh.service.TestService;
import com.tlh.utils.UrlUtils;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private ChainService chainService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private TestService testService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Course> getCourses(String kw, Long chainId, Long regionId, User caller, Integer page, Integer size, Boolean activeOnly) {
        if (caller != null && "EMPLOYEE".equals(caller.getRole())) {
            return this.courseRepo.getCoursesForEmployee(kw, caller.getId(), page, size);
        }
        if (caller != null && "TRAINER".equals(caller.getRole())) {
            return this.courseRepo.getCoursesForTrainer(kw, chainId, regionId, caller.getStoreId(), page, size);
        }
        Map<String, String> params = new HashMap<>();
        if (kw != null) {
            params.put("kw", kw);
        }
        if (chainId != null) {
            params.put("chainId", String.valueOf(chainId));
        }
        if (regionId != null) {
            params.put("regionId", String.valueOf(regionId));
        }
        if (activeOnly == null || activeOnly) {
            params.put("activeOnly", "true");
        }
        if (page != null && size != null) {
            params.put("page", String.valueOf(page));
            params.put("size", String.valueOf(size));
        }
        return this.courseRepo.getCourses(params);
    }

    @Override
    public Course getCourseById(long id) {
        return this.courseRepo.getCourseById(id);
    }

    @Override
    public List<Course> getMyCourses(User caller, Integer page, Integer size) {
        if ("ADMIN".equals(caller.getRole())) {
            Map<String, String> params = new HashMap<>();
            if (page != null && size != null) {
                params.put("page", String.valueOf(page));
                params.put("size", String.valueOf(size));
            }
            return this.courseRepo.getCourses(params);
        }
        return this.courseRepo.getCoursesByCreator(caller.getId(), page, size);
    }

    @Override
    public Course addOrUpdate(Course c) {
        Course existing = c.getId() == null ? null : this.courseRepo.getCourseByIdForUpdate(c.getId());
        if (c.getId() != null && existing == null) {
            throw new IllegalArgumentException("Không tìm thấy khóa học");
        }
        if (c.getTitle() == null || c.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoá học không được để trống");
        }
        if (c.getTitle().trim().length() > 200) {
            throw new IllegalArgumentException("Tên khoá học tối đa 200 ký tự");
        }
        c.setTitle(c.getTitle().trim());

        if (c.getDescription() != null) {
            String description = c.getDescription().trim();
            if (description.length() > 65535) {
                throw new IllegalArgumentException("Mô tả khóa học quá dài");
            }
            c.setDescription(description.isEmpty() ? null : description);
        }

        c.setImageUrl(UrlUtils.normalizeHttpUrl(c.getImageUrl(), "Link ảnh", 500, false));

        if (c.getChains() != null) {
            for (Chain ch : c.getChains()) {
                if (ch.getId() == null || this.chainService.getChainById(ch.getId()) == null) {
                    throw new IllegalArgumentException("Chuỗi không tồn tại");
                }
            }
        }
        if (c.getRegions() != null) {
            for (Region r : c.getRegions()) {
                if (r.getId() == null || this.regionService.getRegionById(r.getId()) == null) {
                    throw new IllegalArgumentException("Vùng không tồn tại");
                }
            }
        }

        if (existing == null) {
            c.setIsActive(false);
        } else {
            if (this.enrollmentRepo.hasEnrollments(c.getId())
                    && (!sameChainScope(existing, c) || !sameRegionScope(existing, c))) {
                throw new IllegalArgumentException("Khóa học đã có người ghi danh, không thể thay đổi phạm vi Chuỗi/Vùng.");
            }
            if (c.getIsActive()) {
                validateReadyForEnrollment(c.getId());
            }
        }

        this.courseRepo.saveOrUpdate(c);
        return c;
    }

    @Override
    public void validateReadyForEnrollment(long courseId) {
        boolean hasLesson = !this.lessonRepo.getLessonByCourse(courseId).isEmpty();
        boolean hasActiveTest = false;
        for (com.tlh.pojo.Test test : this.testService.getByCourse(courseId)) {
            if (test.getIsActive()) {
                this.testService.validateForActivation(test.getId());
                hasActiveTest = true;
            }
        }
        if (!hasLesson && !hasActiveTest) {
            throw new IllegalArgumentException("Khóa học chưa có bài học hoặc bài kiểm tra đang hoạt động, không thể mở.");
        }
    }

    private boolean sameChainScope(Course first, Course second) {
        Set<Long> firstIds = new HashSet<>();
        Set<Long> secondIds = new HashSet<>();
        if (first.getChains() != null) {
            for (Chain chain : first.getChains()) {
                firstIds.add(chain.getId());
            }
        }
        if (second.getChains() != null) {
            for (Chain chain : second.getChains()) {
                secondIds.add(chain.getId());
            }
        }
        return firstIds.equals(secondIds);
    }

    private boolean sameRegionScope(Course first, Course second) {
        Set<Long> firstIds = new HashSet<>();
        Set<Long> secondIds = new HashSet<>();
        if (first.getRegions() != null) {
            for (Region region : first.getRegions()) {
                firstIds.add(region.getId());
            }
        }
        if (second.getRegions() != null) {
            for (Region region : second.getRegions()) {
                secondIds.add(region.getId());
            }
        }
        return firstIds.equals(secondIds);
    }

    @Override
    public void deactivateCourse(long id) {
        Course course = this.courseRepo.getCourseByIdForUpdate(id);
        if (course == null || !course.getIsActive()) {
            return;
        }
        this.courseRepo.deactivateCourse(id);
        for (com.tlh.pojo.Enrollment enrollment : this.enrollmentRepo.getByCourse(id, null, null)) {
            if (enrollment.getCompletedAt() == null) {
                this.notificationService.create(
                        enrollment.getUserId().getId(),
                        "Khóa học đã được ẩn",
                        "Khóa học " + course.getTitle() + " đã được ẩn khỏi danh mục. Ghi danh của bạn vẫn được giữ để tiếp tục học.",
                        "/courses/" + id);
            }
        }
    }

    @Override
    public boolean canManage(User caller, Course course) {
        if (caller == null || course == null) {
            return false;
        }
        if ("ADMIN".equals(caller.getRole())) {
            return true;
        }
        return "TRAINER".equals(caller.getRole())
                && course.getCreatedBy() != null
                && course.getCreatedBy().getId().equals(caller.getId());
    }

    @Override
    public boolean canView(User caller, Course course) {
        if (caller == null || course == null) {
            return false;
        }
        if (this.canManage(caller, course)) {
            return true;
        }
        if ("EMPLOYEE".equals(caller.getRole())) {
            return this.enrollmentService.isEnrolled(course.getId(), caller.getId());
        }
        if ("TRAINER".equals(caller.getRole())) {
            return course.getIsActive() && this.isStoreInCourseScope(caller.getStoreId(), course);
        }
        return false;
    }

    @Override
    public boolean canAnswerForum(User caller, Course course) {
        if (this.canManage(caller, course)) {
            return true;
        }
        return caller != null && course != null
                && "TRAINER".equals(caller.getRole())
                && course.getIsActive()
                && course.getChains().isEmpty() && course.getRegions().isEmpty();
    }

    private boolean isStoreInCourseScope(Store store, Course course) {
        if (!course.getChains().isEmpty()) {
            if (store == null) {
                return false;
            }
            boolean chainMatch = false;
            for (Chain ch : course.getChains()) {
                if (ch.getId().equals(store.getChainId().getId())) {
                    chainMatch = true;
                    break;
                }
            }
            if (!chainMatch) {
                return false;
            }
        }
        if (!course.getRegions().isEmpty()) {
            if (store == null) {
                return false;
            }
            for (Region r : course.getRegions()) {
                if (r.getId().equals(store.getRegionId().getId())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
