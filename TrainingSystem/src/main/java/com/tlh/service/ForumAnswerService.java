/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.ForumAnswer;
import com.tlh.pojo.User;

/**
 *
 * @author LENOVO
 */
public interface ForumAnswerService {
    ForumAnswer answer(long forumQuestionId, User caller, String content);
}
