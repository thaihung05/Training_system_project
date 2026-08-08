/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.QuestionOption;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface QuestionOptionRepository {
    List<QuestionOption> getByQuestion(long questionId);
    QuestionOption getById(long id);
    int getMaxOrderIndex(long questionId);
    void saveOrUpdate(QuestionOption o);
    void delete(long id);
    void clearCorrectForQuestion(long questionId);
}
