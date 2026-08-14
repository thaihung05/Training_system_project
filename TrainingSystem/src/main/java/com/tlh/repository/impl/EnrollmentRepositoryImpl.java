/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Enrollment;
import com.tlh.repository.EnrollmentRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
public class EnrollmentRepositoryImpl implements EnrollmentRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Enrollment> getByCourse(long courseId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        Root<Enrollment> root = q.from(Enrollment.class);

        q.select(root).where(b.equal(root.get("courseId").get("id"), courseId));
        q.orderBy(b.desc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            int p = Math.max(page, 1);
            query.setFirstResult((p - 1) * size);
            query.setMaxResults(size);
        }
        return query.getResultList();
    }

    @Override
    public List<Enrollment> getByUser(long userId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        Root<Enrollment> root = q.from(Enrollment.class);

        q.select(root).where(b.equal(root.get("userId").get("id"), userId));
        q.orderBy(b.desc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            int p = Math.max(page, 1);
            query.setFirstResult((p - 1) * size);
            query.setMaxResults(size);
        }
        return query.getResultList();
    }

    @Override
    public Enrollment getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Enrollment.class, id);
    }

    @Override
    public Enrollment getByCourseAndUser(long courseId, long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        Root<Enrollment> root = q.from(Enrollment.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("courseId").get("id"), courseId));
        predicates.add(b.equal(root.get("userId").get("id"), userId));
        q.select(root).where(predicates.toArray(Predicate[]::new));
        List<Enrollment> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Enrollment getByCourseAndUserForUpdate(long courseId, long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        Root<Enrollment> root = q.from(Enrollment.class);
        q.select(root).where(
                b.equal(root.get("courseId").get("id"), courseId),
                b.equal(root.get("userId").get("id"), userId));
        List<Enrollment> results = s.createQuery(q)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean hasEnrollments(long courseId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Enrollment> root = q.from(Enrollment.class);
        q.select(b.count(root)).where(b.equal(root.get("courseId").get("id"), courseId));
        return s.createQuery(q).getSingleResult() > 0;
    }

    @Override
    public void saveOrUpdate(Enrollment e) {
        Session s = this.factory.getObject().getCurrentSession();
        if (e.getId() == null)
            s.persist(e);
        else
            s.merge(e);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Enrollment e = s.get(Enrollment.class, id);
        if (e!=null)
            s.remove(e);
    }
    
}
