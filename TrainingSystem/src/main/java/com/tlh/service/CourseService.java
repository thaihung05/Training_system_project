/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Course;
import com.tlh.pojo.User;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface CourseService {
    List<Course> getCourses(String kw, Long chainId, Long regionId, User caller, Integer page, Integer size, Boolean activeOnly);
    Course getCourseById(long id);
    List<Course> getMyCourses(User caller, Integer page, Integer size);
    Course addOrUpdate(Course c);
    void deactivateCourse(long id);
    boolean canManage(User caller, Course course);
    boolean canView(User caller, Course course);
    boolean canAnswerForum(User caller, Course course);
}
