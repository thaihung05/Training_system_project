/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.UserBadge;
import com.tlh.repository.UserBadgeRepository;
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
public class UserBadgeRepositoryImpl implements UserBadgeRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<UserBadge> getByUser(long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<UserBadge> q = b.createQuery(UserBadge.class);
        Root<UserBadge> root = q.from(UserBadge.class);
        q.select(root).where(b.equal(root.get("userId").get("id"), userId));
        q.orderBy(b.desc(root.get("id")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public UserBadge getByUserAndBadge(long userId, long badgeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<UserBadge> q = b.createQuery(UserBadge.class);
        Root<UserBadge> root = q.from(UserBadge.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("userId").get("id"), userId));
        predicates.add(b.equal(root.get("badgeId").get("id"), badgeId));

        q.select(root).where(predicates.toArray(Predicate[]::new));

        List<UserBadge> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(UserBadge b) {
        Session s = this.factory.getObject().getCurrentSession();
        if (b.getId() == null)
            s.persist(b);
        else
            s.merge(b);
    }
    
}
