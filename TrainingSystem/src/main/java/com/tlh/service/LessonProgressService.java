/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.LessonProgress;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface LessonProgressService {
    List<LessonProgress> getByEnrollment(long enrollmentId);
    LessonProgress getById(long id);
    LessonProgress markComplete(long id);   
}
