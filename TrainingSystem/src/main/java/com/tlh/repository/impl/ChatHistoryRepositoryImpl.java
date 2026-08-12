/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.ChatHistory;
import com.tlh.repository.ChatHistoryRepository;
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
public class ChatHistoryRepositoryImpl implements ChatHistoryRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<ChatHistory> getByUser(long userId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ChatHistory> q = b.createQuery(ChatHistory.class);
        Root<ChatHistory> root = q.from(ChatHistory.class);
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
    public List<ChatHistory> getBySession(String sessionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ChatHistory> q = b.createQuery(ChatHistory.class);
        Root<ChatHistory> root = q.from(ChatHistory.class);
        q.select(root).where(b.equal(root.get("sessionId"), sessionId));
        q.orderBy(b.asc(root.get("id")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public List<ChatHistory> getPending(Long departmentId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ChatHistory> q = b.createQuery(ChatHistory.class);
        Root<ChatHistory> root = q.from(ChatHistory.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.isNull(root.get("answer")));
        if (departmentId != null) {
            predicates.add(b.equal(root.get("userId").get("departmentId").get("id"), departmentId));
        }
        q.select(root).where(predicates.toArray(Predicate[]::new));
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
    public ChatHistory getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(ChatHistory.class, id);
    }

    @Override
    public void saveOrUpdate(ChatHistory c) {
        Session s = this.factory.getObject().getCurrentSession();
        if (c.getId() == null)
            s.persist(c);
        else
            s.merge(c);
    }
    
}
