/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.ForumAnswer;
import com.tlh.repository.ForumAnswerRepository;
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
public class ForumAnswerRepositoryImpl implements ForumAnswerRepository{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ForumAnswer> getByQuestion(long forumQuestionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ForumAnswer> q = b.createQuery(ForumAnswer.class);
        Root<ForumAnswer> root = q.from(ForumAnswer.class);
        q.select(root).where(b.equal(root.get("forumQuestionId").get("id"), forumQuestionId));
        q.orderBy(b.asc(root.get("id")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public void saveOrUpdate(ForumAnswer a) {
        Session s = this.factory.getObject().getCurrentSession();
        if (a.getId() == null)
            s.persist(a);
        else
            s.merge(a);
    }

}
