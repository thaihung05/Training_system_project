/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Question;
import com.tlh.repository.QuestionRepository;
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
public class QuestionRepositoryImpl implements QuestionRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Question> getByTest(long testId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Question> q = b.createQuery(Question.class);
        Root<Question> root = q.from(Question.class);

        q.select(root).where(b.equal(root.get("testId").get("id"), testId));
        q.orderBy(b.asc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            int p = Math.max(page, 1);
            query.setFirstResult((p - 1) * size);
            query.setMaxResults(size);
        }
        return query.getResultList();
    }

    @Override
    public List<Question> getActiveByTest(long testId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Question> q = b.createQuery(Question.class);
        Root<Question> root = q.from(Question.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("testId").get("id"), testId));
        predicates.add(b.isTrue(root.get("isActive")));
        
        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.asc(root.get("id")));
        return s.createQuery(q).getResultList();
        
    }

    @Override
    public Question getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Question.class, id);
    }

    @Override
    public void saveOrUpdate(Question q) {
        Session s = this.factory.getObject().getCurrentSession();
        if (q.getId()==null)
            s.persist(q);
        else
            s.merge(q);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Question q = s.get(Question.class, id);
        if (q != null)
            s.remove(q);
    }
    
}
