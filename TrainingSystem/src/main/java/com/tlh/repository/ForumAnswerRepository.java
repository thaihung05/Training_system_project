/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.ForumAnswer;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ForumAnswerRepository {
    List<ForumAnswer> getByQuestion(long forumQuestionId);
    void saveOrUpdate(ForumAnswer a);
}
