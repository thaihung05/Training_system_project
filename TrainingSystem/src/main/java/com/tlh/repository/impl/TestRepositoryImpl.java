/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Test;
import com.tlh.repository.TestRepository;
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
public class TestRepositoryImpl implements TestRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Test> getByCourse(long courseId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Test> q = b.createQuery(Test.class);
        Root<Test> root = q.from(Test.class);

        q.select(root).where(b.equal(root.get("courseId").get("id"), courseId));
        q.orderBy(b.desc(root.get("id")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public Test getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Test.class, id);
    }

    @Override
    public void saveOrUpdate(Test t) {
        Session s = this.factory.getObject().getCurrentSession();
        if (t.getId() == null)
            s.persist(t);
        else
            s.merge(t);
    }
}
