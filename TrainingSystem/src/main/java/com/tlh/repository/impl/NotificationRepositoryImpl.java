/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Notification;
import com.tlh.repository.NotificationRepository;
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
public class NotificationRepositoryImpl implements NotificationRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Notification> getByUser(long userId, boolean unreadOnly, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Notification> q = b.createQuery(Notification.class);
        Root<Notification> root = q.from(Notification.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("userId").get("id"), userId));
        if (unreadOnly) 
            predicates.add(b.isFalse(root.get("isRead")));
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
    public long countUnread(long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Notification> root = q.from(Notification.class);
        q.select(b.count(root)).where(
                b.equal(root.get("userId").get("id"), userId),
                b.isFalse(root.get("isRead")));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Notification getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Notification.class, id);
    }

    @Override
    public void saveOrUpdate(Notification n) {
        Session s = this.factory.getObject().getCurrentSession();
        if (n.getId() == null)
            s.persist(n);
        else 
            s.merge(n);
    }

    @Override
    public int markAllRead(long userId) {
        List<Notification> unread = this.getByUser(userId, true, null, null);
        Session s = this.factory.getObject().getCurrentSession();
        for (Notification n : unread){
            n.setIsRead(true);
            s.merge(n);
        }
        return unread.size();
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Notification n = s.get(Notification.class, id);
        if (n!=null)
            s.remove(n);
    }   
}
