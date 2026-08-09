/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.ChatHistory;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface ChatHistoryRepository {
    List<ChatHistory> getByUser(long userId, Integer page, Integer size);
    List<ChatHistory> getBySession(String sessionId);
    List<ChatHistory> getPending(Long departmentId, Integer page, Integer size);
    ChatHistory getById(long id);
    void saveOrUpdate(ChatHistory c);
}
