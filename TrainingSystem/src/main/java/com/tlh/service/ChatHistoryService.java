/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.ChatHistory;
import com.tlh.pojo.User;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ChatHistoryService {
    ChatHistory ask(User caller, String question, String sessionId);
    List<ChatHistory> getMyHistory(long userId, Integer page, Integer size);
    List<ChatHistory> getBySession(String sessionId);
    List<ChatHistory> getPending(User caller, Integer page, Integer size);
    List<ChatHistory> getAnswered(User caller, Integer page, Integer size);
    ChatHistory getById(long id);
    ChatHistory answer(long id, User caller, String answertext);
}
