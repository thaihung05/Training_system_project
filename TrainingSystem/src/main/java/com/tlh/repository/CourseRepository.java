/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Course;
import java.util.List;
import java.util.Map;
/**
 *
 * @author LENOVO
 */
public interface CourseRepository {
    List<Course> getCourses(Map<String, String> params);
    List<Course> getCoursesForEmployee(String kw, Long employeeDepartmentId);
    Course getCourseById(long id);
    List<Course> getCoursesByCreator(long userId);
    void saveOrUpdate(Course c);
    void deactivateCourse(long id);
}
