/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.QuestionOption;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface QuestionOptionService {
    List<QuestionOption> getByQuestion(long questionId);
    QuestionOption getById(long id);
    QuestionOption addOption(QuestionOption o);
    QuestionOption updateOption(QuestionOption o);
    void deleteOption(long id);
    List<QuestionOption> setCorrectOption(long questionId, long optionId);
}
