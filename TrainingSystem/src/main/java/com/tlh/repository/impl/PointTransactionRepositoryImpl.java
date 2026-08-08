/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.PointTransaction;
import com.tlh.repository.PointTransactionRepository;
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
public class PointTransactionRepositoryImpl implements PointTransactionRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<PointTransaction> getByUser(long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<PointTransaction> q = b.createQuery(PointTransaction.class);
        Root<PointTransaction> root = q.from(PointTransaction.class);
        q.select(root).where(b.equal(root.get("userId").get("id"), userId));
        q.orderBy(b.desc(root.get("id")));
        return s.createQuery(q).getResultList();
    }

    @Override
    public long getTotalPoints(long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Integer> q = b.createQuery(Integer.class);
        Root<PointTransaction> root = q.from(PointTransaction.class);

        q.select(b.sum(root.get("points"))).where(b.equal(root.get("userId").get("id"), userId));
        Integer total = s.createQuery(q).getSingleResult();
        return total != null ? total.longValue() : 0L;
    }

    @Override
    public long getTotalPointsIssued() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Integer> q = b.createQuery(Integer.class);
        Root<PointTransaction> root = q.from(PointTransaction.class);

        q.select(b.sum(root.get("points")));
        Integer total = s.createQuery(q).getSingleResult();
        return total != null ? total.longValue() : 0L;
    }

    @Override
    public void saveOrUpdate(PointTransaction t) {
        Session s = this.factory.getObject().getCurrentSession();
        if (t.getId() == null)
            s.persist(t);
        else
            s.merge(t);
    }
    
}
