/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.pojo.Lesson;
import com.tlh.pojo.LessonProgress;
import com.tlh.repository.EnrollmentRepository;
import com.tlh.repository.LessonProgressRepository;
import com.tlh.repository.LessonRepository;
import com.tlh.service.CourseService;
import com.tlh.service.LessonService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class LessonServiceImpl implements LessonService{
    
    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private LessonProgressRepository lessonProgressRepo;

    @Override
    public List<Lesson> getLessonByCourse(long courseId) {
        return this.lessonRepo.getLessonByCourse(courseId);
    }

    @Override
    public Lesson getLessonById(long id) {
        return this.lessonRepo.getLessonById(id);
    }

    @Override
    public Lesson addLesson(Lesson l) {
        validateTitle(l);
        if (l.getCourseId() == null || l.getCourseId().getId() == null)
            throw new IllegalArgumentException("Vui lòng chọn khoá học cho bài học này");
        Course c = this.courseService.getCourseById(l.getCourseId().getId());
        if (c == null)
            throw new IllegalArgumentException("Khoá học không tồn tại");
        int maxOrder = this.lessonRepo.getMaxOrderIndex(c.getId());
        l.setOrderIndex(maxOrder + 1);
        this.lessonRepo.saveOrUpdate(l);
        
        for (Enrollment e : this.enrollmentRepo.getByCourse(c.getId(), null, null)){
            if (e.getCompletedAt() != null)
                continue;
            LessonProgress lp = new LessonProgress();
            lp.setEnrollmentId(e);
            lp.setLessonId(l);
            lp.setIsCompleted(false);
            this.lessonProgressRepo.saveOrUpdate(lp);
        }
        return l;
    }

    @Override
    public Lesson updateLesson(Lesson l) {
        validateTitle(l);
        this.lessonRepo.saveOrUpdate(l);
        return l;
    }

    @Override
    public void deleteLesson(long id) {
        this.lessonRepo.delete(id);
    }

    @Override
    public List<Lesson> reorder(long courseId, List<Long> orderedLessonIds) {
        List<Lesson> current = this.lessonRepo.getLessonByCourse(courseId);
        if (current.size() != orderedLessonIds.size())
            throw new IllegalArgumentException("Danh sách bài học không khớp với khoá học");
        for (Lesson l : current){
            if (!orderedLessonIds.contains(l.getId()))
                throw new IllegalArgumentException("Danh sách bài học không khớp với khoá học");
        }
        this.lessonRepo.reorder(orderedLessonIds);
        return this.lessonRepo.getLessonByCourse(courseId);
    }
    
    private void validateTitle(Lesson l){
        if (l.getTitle() == null || l.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Tên bài học không được rỗng");
        if (l.getTitle().trim().length() > 200)
            throw new IllegalArgumentException("Tên bài học chỉ được tối đa 200 kí tự");
        l.setTitle(l.getTitle().trim());
    }
    
}
