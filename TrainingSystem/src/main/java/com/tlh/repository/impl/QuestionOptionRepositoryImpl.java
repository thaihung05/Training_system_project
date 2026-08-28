/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.QuestionOption;
import com.tlh.repository.QuestionOptionRepository;
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
public class QuestionOptionRepositoryImpl implements QuestionOptionRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<QuestionOption> getByQuestion(long questionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<QuestionOption> q = b.createQuery(QuestionOption.class);
        Root<QuestionOption> root = q.from(QuestionOption.class);
        
        q.select(root).where(b.equal(root.get("questionId").get("id"), questionId));
        q.orderBy(b.asc(root.get("orderIndex")));
        return s.createQuery(q).getResultList();
        
    }

    @Override
    public QuestionOption getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(QuestionOption.class, id);
    }

    @Override
    public int getMaxOrderIndex(long questionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Integer> q = b.createQuery(Integer.class);
        Root<QuestionOption> root = q.from(QuestionOption.class);
        q.select(b.max(root.get("orderIndex"))).where(b.equal(root.get("questionId").get("id"), questionId));
        Integer max = s.createQuery(q).getSingleResult();
        return max != null ? max : -1;
    }

    @Override
    public void saveOrUpdate(QuestionOption o) {
        Session s = this.factory.getObject().getCurrentSession();
        if (o.getId() == null)
            s.persist(o);
        else
            s.merge(o);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        QuestionOption o = s.get(QuestionOption.class, id);
        if (o!=null)
            s.remove(o);
    }

    @Override
    public void clearCorrectForQuestion(long questionId) {
        List<QuestionOption> options = this.getByQuestion(questionId);
        Session s = this.factory.getObject().getCurrentSession();
        for (QuestionOption o : options){
            if (o.getIsCorrect()) {
                o.setIsCorrect(false);
                s.merge(o);
            }
        }
    }
    
}
