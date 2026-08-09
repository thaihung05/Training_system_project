/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Question;
import com.tlh.pojo.Test;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
public interface QuestionService {
    List<Question> getByTest(long testId, Integer page, Integer size);
    List<Question> getActiveByTest(long testId);
    Question getById(long id);
    Question addQuestion(Question q);
    Question updateQuestion(Question q);
    void deleteQuestion(long id);
    List<Map<String, Object>> getQuestionsForCompose(long testId, Integer page, Integer size);
    List<Map<String, Object>> bulkImportQuestions(Test test, MultipartFile file);
}
