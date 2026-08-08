/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Lesson;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface LessonService {
    List<Lesson> getLessonByCourse(long courseId);
    Lesson getLessonById(long id);
    Lesson addLesson(Lesson l);
    Lesson updateLesson(Lesson l);
    void deleteLesson(long id);
    List<Lesson> reorder(long courseId, List<Long> orderedLessonIds);
}
