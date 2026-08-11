/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.ForumQuestion;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ForumQuestionRepository {
    List<ForumQuestion> getByCourse(long courseId, Integer page, Integer size);
    ForumQuestion getById(long id);
    void saveOrUpdate(ForumQuestion q);
}
