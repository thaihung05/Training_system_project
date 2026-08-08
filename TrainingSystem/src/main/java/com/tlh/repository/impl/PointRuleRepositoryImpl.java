/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.PointRule;
import com.tlh.repository.PointRuleRepository;
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
public class PointRuleRepositoryImpl implements PointRuleRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<PointRule> getAll() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<PointRule> q = b.createQuery(PointRule.class);
        Root<PointRule> root = q.from(PointRule.class);
        q.select(root);
        q.orderBy(b.asc(root.get("actionType")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public PointRule getById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(PointRule.class, id);
    }

    @Override
    public PointRule getByActionType(String actionType) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<PointRule> q = b.createQuery(PointRule.class);
        Root<PointRule> root = q.from(PointRule.class);
        q.select(root).where(b.equal(root.get("actionType"), actionType));
        List<PointRule> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(PointRule r) {
        Session s = this.factory.getObject().getCurrentSession();
        if (r.getId()==null)
            s.persist(r);
        else
            s.merge(r);
    }

    @Override
    public void delete(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        PointRule r = s.get(PointRule.class, id);
        if (r!=null)
            s.remove(r);
    }
    
}
