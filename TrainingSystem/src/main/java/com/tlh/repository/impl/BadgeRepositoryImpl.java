/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Badge;
import com.tlh.repository.BadgeRepository;
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
public class BadgeRepositoryImpl implements BadgeRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Badge> getAll() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Badge> q = b.createQuery(Badge.class);
        Root<Badge> root = q.from(Badge.class);

        q.select(root);
        q.orderBy(b.asc(root.get("name")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public Badge getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Badge.class, id);
    }

    @Override
    public Badge getByCode(String code) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Badge> q = b.createQuery(Badge.class);
        Root<Badge> root = q.from(Badge.class);

        q.select(root).where(b.equal(root.get("code"), code));

        List<Badge> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(Badge b) {
        Session s = this.factory.getObject().getCurrentSession();
        if (b.getId() == null)
            s.persist(b);
        else
            s.merge(b);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Badge b = s.get(Badge.class, id);
        if (b != null) {
            s.remove(b);
        }
    }

}
