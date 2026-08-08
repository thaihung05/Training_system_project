/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.LessonProgress;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface LessonProgressRepository {
    List<LessonProgress> getByEnrollment(long enrollmentId);
    LessonProgress getById(long id);
    void saveOrUpdate(LessonProgress lp);
}
