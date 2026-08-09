/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.User;
import com.tlh.repository.CourseRepository;
import com.tlh.service.CourseService;
import com.tlh.service.DepartmentService;
import com.tlh.service.EnrollmentService;
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
    private DepartmentService departmentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Override
    public List<Course> getCourses(String kw, Long departmentId, User caller, Integer page, Integer size) {
        if (caller != null && "EMPLOYEE".equals(caller.getRole())) {
            Long employeeDepartmentId = caller.getDepartmentId() != null ? caller.getDepartmentId().getId() : null;
            return this.courseRepo.getCoursesForEmployee(kw, employeeDepartmentId, page, size);
        }
        Map<String, String> params = new HashMap<>();
        if (kw != null) {
            params.put("kw", kw);
        }
        if (departmentId != null) {
            params.put("departmentId", String.valueOf(departmentId));
        }
        params.put("activeOnly", "true");
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

        if (c.getDepartmentId() != null && c.getDepartmentId().getId() != null
                && this.departmentService.getDepartmentById(c.getDepartmentId().getId()) == null) {
            throw new IllegalArgumentException("Phòng ban không tồn tại");
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
        if (!"EMPLOYEE".equals(caller.getRole())) {
            return true;
        }
        if (!course.getIsActive() && !this.enrollmentService.isEnrolled(course.getId(), caller.getId())) {
            return false;
        }
        if (course.getDepartmentId() == null) {
            return true;
        }
        if (caller.getDepartmentId() == null) {
            return false;
        }
        return course.getDepartmentId().getId().equals(caller.getDepartmentId().getId());
    }
}
