/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.LessonProgress;
import com.tlh.repository.LessonProgressRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Repository
@Transactional
public class LessonProgressRepositoryImpl implements LessonProgressRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<LessonProgress> getByEnrollment(long enrollmentId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<LessonProgress> q = b.createQuery(LessonProgress.class);
        Root<LessonProgress> root = q.from(LessonProgress.class);
        
        q.select(root).where(b.equal(root.get("enrollmentId").get("id"), enrollmentId));
        q.orderBy(b.asc(root.get("lessonId").get("orderIndex")));
        
        return s.createQuery(q).getResultList();
    }

    @Override
    public List<LessonProgress> getByLesson(long lessonId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<LessonProgress> q = b.createQuery(LessonProgress.class);
        Root<LessonProgress> root = q.from(LessonProgress.class);

        q.select(root).where(b.equal(root.get("lessonId").get("id"), lessonId));

        return s.createQuery(q).getResultList();
    }

    @Override
    public LessonProgress getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(LessonProgress.class, id);
    }

    @Override
    public void saveOrUpdate(LessonProgress lp) {
        Session s = this.factory.getObject().getCurrentSession();
        if (lp.getId() == null)
            s.persist(lp);
        else
            s.merge(lp);
    }
    
}
