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
    List<Enrollment> getByCourse(long courseId);
    List<Enrollment> getByUser(long userId);
    Enrollment getById(long id);
    Enrollment getByCourseAndUser(long courseId, long userId);
    void saveOrUpdate(Enrollment e);
    void delete(long id);
}
