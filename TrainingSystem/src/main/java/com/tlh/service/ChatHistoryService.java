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
    List<ChatHistory> getMyHistory(long userId);
    List<ChatHistory> getBySession(String sessionId);
    List<ChatHistory> getPending();
    ChatHistory getById(long id);
    ChatHistory answer(long id, String answertext);
}
