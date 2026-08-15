/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Course;
import com.tlh.pojo.Store;
import java.util.List;
import java.util.Map;
/**
 *
 * @author LENOVO
 */
public interface CourseRepository {
    List<Course> getCourses(Map<String, String> params);
    List<Course> getCoursesForEmployee(String kw, Long employeeId, Integer page, Integer size);
    List<Course> getCoursesForTrainer(String kw, Long chainId, Long regionId, Store store, Integer page, Integer size);
    Course getCourseById(long id);
    Course getCourseByIdForUpdate(long id);
    List<Course> getCoursesByCreator(long userId, Integer page, Integer size);
    long countByChain(long chainId);
    long countByRegion(long regionId);
    void saveOrUpdate(Course c);
    void deactivateCourse(long id);
}
