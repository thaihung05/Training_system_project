/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Certificate;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface CertificateRepository {
    List<Certificate> getByUser(long userId);
    List<Certificate> getByCourse(long courseId);
    Certificate getById(long id);
    Certificate getByUserAndCourse(long userId, long courseId);
    long countByUser(long userId);
    void saveOrUpdate(Certificate c);
}
