/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Test;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface TestService {
    List<Test> getByCourse(long courseId);
    Test getById(long id);
    Test addTest(Test t);
    Test updateTest(Test t);
    void validateForActivation(long testId);
}
