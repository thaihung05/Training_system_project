/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Test;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface TestRepository {
    List<Test> getByCourse(long courseId);
    Test getById(long id);
    void saveOrUpdate(Test t);
}
