/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Store;
import com.tlh.repository.StoreRepository;
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
public class StoreRepositoryImpl implements StoreRepository{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Store> getStores() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Store> q = b.createQuery(Store.class);
        Root<Store> root = q.from(Store.class);

        q.select(root);
        q.orderBy(b.asc(root.get("name")));

        return s.createQuery(q).getResultList();
    }

    @Override
    public Store getStoreById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Store.class, id);
    }

    @Override
    public Store getStoreByName(String name) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Store> q = b.createQuery(Store.class);
        Root<Store> root = q.from(Store.class);

        q.select(root);
        q.where(b.equal(root.get("name"), name));

        List<Store> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(Store store) {
        Session s = this.factory.getObject().getCurrentSession();
        if (store.getId() == null)
            s.persist(store);
        else
            s.merge(store);
    }

    @Override
    public void deleteStore(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Store store = s.get(Store.class, id);
        if (store != null) {
            s.remove(store);
        }
    }

}
