/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Lesson;
import com.tlh.repository.LessonRepository;
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
public class LessonRepositoryImpl implements LessonRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Lesson> getLessonByCourse(long courseId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Lesson> q = b.createQuery(Lesson.class);
        Root<Lesson> root = q.from(Lesson.class);
        
        q.select(root).where(b.equal(root.get("courseId").get("id"), courseId));
        q.orderBy(b.asc(root.get("orderIndex")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public Lesson getLessonById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Lesson.class, id);
    }

    @Override
    public void saveOrUpdate(Lesson l) {
        Session s = this.factory.getObject().getCurrentSession();
        if (l.getId() == null)
            s.persist(l);
        else 
            s.merge(l);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Lesson l = s.get(Lesson.class, id);
        if (l != null)
            s.remove(l);
    }

    @Override
    public int getMaxOrderIndex(long courseId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Integer> q = b.createQuery(Integer.class);
        Root<Lesson> root = q.from(Lesson.class);
        
        q.select(b.max(root.get("orderIndex"))).where(b.equal(root.get("courseId").get("id"), courseId));
        Integer max = s.createQuery(q).getSingleResult();
        return max!=null ? max : -1;
    }

    @Override
    public void reorder(List<Long> orderedLessonIds) {
        Session s = this.factory.getObject().getCurrentSession();
        int tmp = orderedLessonIds.size();
        for (int i=0;i<orderedLessonIds.size(); i++){
            Lesson l = s.get(Lesson.class, orderedLessonIds.get(i));
            if (l!=null){
                l.setOrderIndex(tmp + i);
                s.merge(l);
            }
        }
        s.flush();
        for (int i=0;i<orderedLessonIds.size(); i++){
            Lesson l = s.get(Lesson.class, orderedLessonIds.get(i));
            if (l!=null){
                l.setOrderIndex(i);
                s.merge(l);
            }
        }
    }
    
}
