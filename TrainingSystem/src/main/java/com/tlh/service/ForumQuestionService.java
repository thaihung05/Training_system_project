/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.ForumQuestion;
import com.tlh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public interface ForumQuestionService {
    ForumQuestion ask(long courseId, User caller, String content);
    List<Map<String, Object>> getByCourse(long courseId, Integer page, Integer size);
    ForumQuestion getById(long id);
    boolean hasPendingQuestion(long courseId, long userId);
}
