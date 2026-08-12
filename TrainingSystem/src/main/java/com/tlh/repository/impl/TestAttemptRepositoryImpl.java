/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.AttemptAnswer;
import com.tlh.pojo.TestAttempt;
import com.tlh.repository.TestAttemptRepository;
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
public class TestAttemptRepositoryImpl implements TestAttemptRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<TestAttempt> getByUserAndTest(long userId, long testId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<TestAttempt> q = b.createQuery(TestAttempt.class);
        Root<TestAttempt> root = q.from(TestAttempt.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("userId").get("id"), userId));
        predicates.add(b.equal(root.get("testId").get("id"), testId));

        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.asc(root.get("attemptNo")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public TestAttempt getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(TestAttempt.class, id);
    }

    @Override
    public List<TestAttempt> getByTest(long testId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<TestAttempt> q = b.createQuery(TestAttempt.class);
        Root<TestAttempt> root = q.from(TestAttempt.class);

        q.select(root).where(b.equal(root.get("testId").get("id"), testId));
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
    public List<TestAttempt> getByUser(long userId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<TestAttempt> q = b.createQuery(TestAttempt.class);
        Root<TestAttempt> root = q.from(TestAttempt.class);

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
    public boolean hasAttempts(long testId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<TestAttempt> root = q.from(TestAttempt.class);
        q.select(b.count(root)).where(b.equal(root.get("testId").get("id"), testId));
        return s.createQuery(q).getSingleResult() > 0;
    }

    @Override
    public void saveOrUpdate(TestAttempt a) {
        Session s = this.factory.getObject().getCurrentSession();
        if (a.getId() == null)
            s.persist(a);
        else
            s.merge(a);
    }

    @Override
    public List<AttemptAnswer> getAnswers(long attemptId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<AttemptAnswer> q = b.createQuery(AttemptAnswer.class);
        Root<AttemptAnswer> root = q.from(AttemptAnswer.class);

        q.select(root).where(b.equal(root.get("attemptId").get("id"), attemptId));

        return s.createQuery(q).getResultList();
    }
    
}
