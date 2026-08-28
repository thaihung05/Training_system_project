/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public interface EnrollmentService {
    Map<String, Object> enrollUsers(Course course, List<Long> userIds);
    Map<String, Object> enrollStore(Course course, long storeId);
    List<Enrollment> getRoster(long courseId, Integer page, Integer size);
    List<Enrollment> getMyEnrollments(long userId, Integer page, Integer size);
    Enrollment getById(long id);
    void unenroll(long id);
    void recalcProgress(long enrollmentId);
    boolean isEnrolled(long courseId, long userId);
}
