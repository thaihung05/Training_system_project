/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.AttemptAnswer;
import com.tlh.pojo.TestAttempt;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface TestAttemptRepository {
    List<TestAttempt> getByUserAndTest(long userId, long testId);
    TestAttempt getById(long id);
    List<TestAttempt> getByTest(long testId, Integer page, Integer size);
    boolean hasAttempts(long testId);
    List<TestAttempt> getByUser(long userId, Integer page, Integer size);
    void saveOrUpdate(TestAttempt a);
    List<AttemptAnswer> getAnswers(long attemptId);
}
