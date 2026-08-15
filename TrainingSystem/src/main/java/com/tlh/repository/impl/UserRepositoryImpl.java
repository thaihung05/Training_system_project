/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.User;
import com.tlh.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        
        q.select(root);
        String kw = params != null ? params.get("kw") : null;
        if (kw != null && !kw.trim().isEmpty()) {
            String pattern = "%" + kw.trim().toLowerCase() + "%";
            Predicate byName = b.like(b.lower(root.get("name")), pattern);
            Predicate byEmail = b.like(b.lower(root.get("email")), pattern);
            q.where(b.or(byName, byEmail));
        }
        
        q.orderBy(b.asc(root.get("id")));
        
        var query = s.createQuery(q);
        String pageStr = params != null ? params.get("page") : null;
        String sizeStr = params != null ? params.get("size") : null;
        if (pageStr != null && sizeStr != null) {
            int page = Math.max(Integer.parseInt(pageStr), 1);
            int size = Integer.parseInt(sizeStr);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
        }
        
        return query.getResultList();
    }

    @Override
    public long countUsers(String kw) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<User> root = q.from(User.class);
        
        if (kw != null && !kw.trim().isEmpty()) {
            String pattern = "%" + kw.trim().toLowerCase() + "%";
            Predicate byName = b.like(b.lower(root.get("name")), pattern);
            Predicate byEmail = b.like(b.lower(root.get("email")), pattern);
            q.where(b.or(byName, byEmail));
        }
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public long countByStore(long storeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<User> root = q.from(User.class);
        q.select(b.count(root)).where(b.equal(root.get("storeId").get("id"), storeId));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public User getUserById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);

        q.select(root);
        q.where(b.equal(root.get("email"), email));

        List<User> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);

        q.select(root);
        q.where(b.equal(root.get("username"), username));

        List<User> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<User> getUserByRole(String role) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("role"), role));
        predicates.add(b.isTrue(root.get("isActive")));
        
        q.select(root);
        q.where(predicates.toArray(Predicate[]::new));
        
        return s.createQuery(q).getResultList();
        
    }

    @Override
    public List<User> getUserByStore(String storeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("storeId").get("id"), Long.parseLong(storeId)));
        predicates.add(b.isTrue(root.get("isActive")));

        q.select(root);
        q.where(predicates.toArray(Predicate[]::new));

        return s.createQuery(q).getResultList();
    }

    @Override
    public void saveOrUpdate(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        if (u.getId() == null){
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            u.setIsActive(true);
            s.persist(u);
        }
        else 
            s.merge(u);
    }

    @Override
    public void deactivateUser(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        User u = s.get(User.class, id);
        if (u!=null){
            u.setIsActive(false);
            s.merge(u);
        }
    }
    
    @Override
    public boolean authenticate(String username, String rawPassword) {
        User u = this.getUserByUsername(username);
        if (u == null || !u.getIsActive()) {
            return false;
        }
        return this.passwordEncoder.matches(rawPassword, u.getPassword());
    }
}
