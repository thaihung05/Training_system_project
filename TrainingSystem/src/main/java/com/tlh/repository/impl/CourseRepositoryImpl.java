/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.Enrollment;
import com.tlh.repository.CourseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
        String chainId = params != null ? params.get("chainId") : null;
        if (chainId != null && !chainId.trim().isEmpty()) {
            predicates.add(b.equal(root.join("chains").get("id"), Long.parseLong(chainId)));
        }
        String regionId = params != null ? params.get("regionId") : null;
        if (regionId != null && !regionId.trim().isEmpty()) {
            predicates.add(b.equal(root.join("regions").get("id"), Long.parseLong(regionId)));
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
            int page = Math.max(Integer.parseInt(pageStr), 1);
            int size = Integer.parseInt(sizeStr);
            query.setFirstResult((page-1) * size);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }
    
    @Override
    public List<Course> getCoursesForEmployee(String kw, Long employeeId, Integer page, Integer size) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.isTrue(root.get("isActive")));
        if (kw != null && !kw.trim().isEmpty()) {
            predicates.add(b.like(b.lower(root.get("title")), "%" + kw.trim().toLowerCase() + "%"));
        }

        Subquery<Long> enrolledCourseIds = q.subquery(Long.class);
        Root<Enrollment> enrollRoot = enrolledCourseIds.from(Enrollment.class);
        enrolledCourseIds.select(enrollRoot.get("courseId").get("id"))
                .where(b.equal(enrollRoot.get("userId").get("id"), employeeId));
        predicates.add(root.get("id").in(enrolledCourseIds));

        q.select(root).where(predicates.toArray(Predicate[]::new));
        q.orderBy(b.desc(root.get("id")));

        var query = s.createQuery(q);
        if (page != null && size != null) {
            int p = Math.max(page, 1);
            query.setFirstResult((p - 1) * size);
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
            int p = Math.max(page, 1);
            query.setFirstResult((p - 1) * size);
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
