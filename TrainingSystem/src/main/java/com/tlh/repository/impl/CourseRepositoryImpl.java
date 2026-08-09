/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Course;
import com.tlh.repository.CourseRepository;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Repository
@Transactional
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);

        List<Predicate> predicates = new ArrayList<>();
        String kw = params != null ? params.get("kw") : null;
        if (kw != null && !kw.trim().isEmpty()) {
            predicates.add(b.like(b.lower(root.get("title")), "%" + kw.trim().toLowerCase() + "%"));
        }
        String departmentId = params != null ? params.get("departmentId") : null;
        if (departmentId != null && !departmentId.trim().isEmpty()) {
            predicates.add(b.equal(root.get("departmentId").get("id"), Long.parseLong(departmentId)));
        }
        if (params != null && "true".equals(params.get("activeOnly"))) {
            predicates.add(b.isTrue(root.get("isActive")));
        }

        q.select(root);
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        q.orderBy(b.desc(root.get("id")));
        
        var query = s.createQuery(q);
        String pageStr = params != null ? params.get("page") : null;
        String sizeStr = params != null ? params.get("size") : null;
        if (pageStr != null && sizeStr != null){
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);
            query.setFirstResult((page-1) * size);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }
    
    @Override
    public List<Course> getCoursesForEmployee(String kw, Long employeeDepartmentId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.isTrue(root.get("isActive")));
        if (kw != null && !kw.trim().isEmpty()) {
            predicates.add(b.like(b.lower(root.get("title")), "%" + kw.trim().toLowerCase() + "%"));
        }

        Predicate noDepartment = b.isNull(root.get("departmentId"));
        if (employeeDepartmentId != null) {
            Predicate sameDepartment = b.equal(root.get("departmentId").get("id"), employeeDepartmentId);
            predicates.add(b.or(noDepartment, sameDepartment));
        } else {
            predicates.add(noDepartment);
        }

        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public Course getCourseById(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Course.class, id);
    }

    @Override
    public List<Course> getCoursesByCreator(long userId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);

        q.select(root).where(b.equal(root.get("createdBy").get("id"), userId));
        q.orderBy(b.desc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public void saveOrUpdate(Course c) {
        Session s = this.factory.getObject().getCurrentSession();
        if (c.getId() == null) {
            s.persist(c);
        } else {
            s.merge(c);
        }
    }

    @Override
    public void deactivateCourse(long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Course c = s.get(Course.class, id);
        if (c != null) {
            c.setIsActive(false);
            s.merge(c);
        }
    }
}
