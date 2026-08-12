/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.ForumQuestion;
import com.tlh.repository.ForumQuestionRepository;
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
public class ForumQuestionRepositoryImpl implements ForumQuestionRepository{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumQuestion> getByCourse(long courseId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ForumQuestion> q = b.createQuery(ForumQuestion.class);
        Root<ForumQuestion> root = q.from(ForumQuestion.class);
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
    public ForumQuestion getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(ForumQuestion.class, id);
    }

    @Override
    public void saveOrUpdate(ForumQuestion q) {
        Session s = this.factory.getObject().getCurrentSession();
        if (q.getId() == null)
            s.persist(q);
        else
            s.merge(q);
    }

}
