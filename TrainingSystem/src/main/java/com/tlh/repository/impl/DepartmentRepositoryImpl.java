/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Department;
import com.tlh.repository.DepartmentRepository;
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
public class DepartmentRepositoryImpl implements DepartmentRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Department> getDepartments() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Department> q = b.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        
        q.select(root);
        q.orderBy(b.asc(root.get("name")));
        
        return s.createQuery(q).getResultList();
    }

    @Override
    public Department getDepartmentById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Department.class, id);
    }

    @Override
    public Department getDepartmentByName(String name) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Department> q = b.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        
        q.select(root);
        q.where(b.equal(root.get("name"), name));
        
        List<Department> results = s.createQuery(q).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveOrUpdate(Department d) {
        Session s = this.factory.getObject().getCurrentSession();
        if (d.getId() == null) 
            s.persist(d);
        else
            s.merge(d);
    }

    @Override
    public void deleteDepartment(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Department d = s.get(Department.class, id);
        if (d != null) {
            s.remove(d);
        }
    }
    
}
