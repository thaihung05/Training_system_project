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
import com.tlh.service.EnrollmentService;
import com.tlh.service.LessonService;
import com.tlh.utils.UrlUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class LessonServiceImpl implements LessonService{
    
    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private LessonProgressRepository lessonProgressRepo;

    @Autowired
    private EnrollmentService enrollmentService;

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
        assertCourseEditable(c.getId());
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
            this.enrollmentService.recalcProgress(e.getId());
        }
        return l;
    }

    @Override
    public Lesson updateLesson(Lesson l) {
        validateTitle(l);
        if (l.getCourseId() == null || l.getCourseId().getId() == null) {
            throw new IllegalArgumentException("Bài học phải thuộc một khóa học");
        }
        assertCourseEditable(l.getCourseId().getId());
        this.lessonRepo.saveOrUpdate(l);
        return l;
    }

    @Override
    public void deleteLesson(long id) {
        Lesson existing = this.lessonRepo.getLessonById(id);
        if (existing == null) {
            return;
        }
        assertCourseEditable(existing.getCourseId().getId());
        Set<Long> affectedEnrollmentIds = new HashSet<>();
        for (LessonProgress lp : this.lessonProgressRepo.getByLesson(id)) {
            affectedEnrollmentIds.add(lp.getEnrollmentId().getId());
        }

        this.lessonRepo.delete(id);

        for (Long enrollmentId : affectedEnrollmentIds) {
            this.enrollmentService.recalcProgress(enrollmentId);
        }
    }

    @Override
    public List<Lesson> reorder(long courseId, List<Long> orderedLessonIds) {
        assertCourseEditable(courseId);
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
        l.setSlidePdfUrl(UrlUtils.normalizeHttpUrl(l.getSlidePdfUrl(), "Link PDF bài học", 500, false));
    }

    private void assertCourseEditable(long courseId) {
        if (this.enrollmentRepo.hasEnrollments(courseId)) {
            throw new IllegalArgumentException("Khóa học đã có người ghi danh, không thể thay đổi bài học. Hãy tạo khóa học mới nếu cần cập nhật nội dung.");
        }
    }
    
}
