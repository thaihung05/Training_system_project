/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Chain;
import com.tlh.repository.ChainRepository;
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
public class ChainRepositoryImpl implements ChainRepository{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Chain> getChains() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Chain> q = b.createQuery(Chain.class);
        Root<Chain> root = q.from(Chain.class);

        q.select(root);
        q.orderBy(b.asc(root.get("name")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public Chain getChainById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Chain.class, id);
    }

    @Override
    public Chain getChainByName(String name) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Chain> q = b.createQuery(Chain.class);
        Root<Chain> root = q.from(Chain.class);

        q.select(root);
        q.where(b.equal(root.get("name"), name));

        List<Chain> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(Chain c) {
        Session s = this.factory.getObject().getCurrentSession();
        if (c.getId() == null)
            s.persist(c);
        else
            s.merge(c);
    }

    @Override
    public void deleteChain(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Chain c = s.get(Chain.class, id);
        if (c != null) {
            s.remove(c);
        }
    }

}
