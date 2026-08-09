/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.TestAttempt;
import com.tlh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public interface TestAttemptService {
    TestAttempt startAttempt(long testId, User caller);
    List<Map<String, Object>> getQuestionsForAttempt(long testId);
    boolean hasOpenAttempt(long testId, long userId);
    Map<String, Object> submit(long attemptId, List<Map<String, Object>> answers);
    Map<String, Object> getAttemptDetail(long attemptId);
    TestAttempt getById(long id);
    List<TestAttempt> getMyAttempts(long userId, Long testId, Integer page, Integer size);
    List<TestAttempt> getByTest(long testId, Integer page, Integer size);
}
