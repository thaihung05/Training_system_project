/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.LessonProgress;
import com.tlh.repository.LessonProgressRepository;
import com.tlh.service.EnrollmentService;
import com.tlh.service.LessonProgressService;
import com.tlh.service.PointTransactionService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class LessonProgressServiceImpl implements LessonProgressService{

    @Autowired
    private LessonProgressRepository lessonProgressRepo;
    
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private PointTransactionService pointTransactionService;
    
    @Override
    public List<LessonProgress> getByEnrollment(long enrollmentId) {
        return this.lessonProgressRepo.getByEnrollment(enrollmentId);
    }

    @Override
    public LessonProgress getById(long id) {
        return this.lessonProgressRepo.getById(id);
    }

    @Override
    public LessonProgress markComplete(long id) {
        LessonProgress lp = this.lessonProgressRepo.getById(id);
        if (lp == null) {
            throw new IllegalArgumentException("Không tìm thấy tiến độ bài học");
        }
        if (lp.getIsCompleted())
            return lp;
        lp.setIsCompleted(true);
        lp.setViewedAt(new Date());
        this.lessonProgressRepo.saveOrUpdate(lp);
        this.enrollmentService.recalcProgress(lp.getEnrollmentId().getId());
        this.pointTransactionService.awardPoints(
                lp.getEnrollmentId().getUserId().getId(),
                "LESSON_COMPLETED",
                "Hoàn thành bài học: " + lp.getLessonId().getTitle());
        return lp;
    }
    
}
