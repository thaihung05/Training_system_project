/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Question;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface QuestionRepository {
    List<Question> getByTest(long testId, Integer page, Integer size);
    List<Question> getActiveByTest(long testId);
    Question getById(long id);
    void saveOrUpdate(Question q);
    void delete(long id);
}
