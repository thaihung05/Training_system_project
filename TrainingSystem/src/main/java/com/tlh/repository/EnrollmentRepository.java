/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Enrollment;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface EnrollmentRepository {
    List<Enrollment> getByCourse(long courseId, Integer page, Integer size);
    List<Enrollment> getByUser(long userId, Integer page, Integer size);
    Enrollment getById(long id);
    Enrollment getByCourseAndUser(long courseId, long userId);
    void saveOrUpdate(Enrollment e);
    void delete(long id);
}
