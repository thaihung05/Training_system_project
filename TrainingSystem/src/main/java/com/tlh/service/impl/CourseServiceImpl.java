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
import com.tlh.service.ChainService;
import com.tlh.service.CourseService;
import com.tlh.service.EnrollmentService;
import com.tlh.service.RegionService;
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
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private ChainService chainService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Override
    public List<Course> getCourses(String kw, Long chainId, Long regionId, User caller, Integer page, Integer size, Boolean activeOnly) {
        if (caller != null && "EMPLOYEE".equals(caller.getRole())) {
            return this.courseRepo.getCoursesForEmployee(kw, caller.getId(), page, size);
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
        if (c.getTitle() == null || c.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoá học không được để trống");
        }
        if (c.getTitle().trim().length() > 200) {
            throw new IllegalArgumentException("Tên khoá học tối đa 200 ký tự");
        }
        c.setTitle(c.getTitle().trim());

        if (c.getImageUrl() != null) {
            String trimmed = c.getImageUrl().trim();
            if (trimmed.length() > 500) {
                throw new IllegalArgumentException("Link ảnh tối đa 500 ký tự");
            }
            c.setImageUrl(trimmed.isEmpty() ? null : trimmed);
        }

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

        this.courseRepo.saveOrUpdate(c);
        return c;
    }

    @Override
    public void deactivateCourse(long id) {
        this.courseRepo.deactivateCourse(id);
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
            return this.isStoreInCourseScope(caller.getStoreId(), course);
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
