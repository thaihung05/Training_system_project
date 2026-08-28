/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Lesson;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface LessonRepository {
    List<Lesson> getLessonByCourse(long courseId);
    Lesson getLessonById(long id);
    void saveOrUpdate(Lesson l);
    void delete(long id);
    int getMaxOrderIndex(long courseId);
    void reorder(List<Long> orderedLessonIds);
}
