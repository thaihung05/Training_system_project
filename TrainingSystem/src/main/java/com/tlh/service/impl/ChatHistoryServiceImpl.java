/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.ChatHistory;
import com.tlh.pojo.User;
import com.tlh.repository.ChatHistoryRepository;
import com.tlh.service.ChatHistoryService;
import com.tlh.service.NotificationService;
import com.tlh.utils.ChatbotClient;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService{

    @Autowired
    private ChatHistoryRepository chatHistoryRepo;

    @Autowired
    private Environment env;

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public ChatHistory ask(User caller, String question, String sessionId) {
        if (question == null || question.trim().isEmpty())
            throw new IllegalArgumentException("Câu hỏi không được để trống");
        
        ChatHistory ch = new ChatHistory();
        ch.setUserId(caller);
        ch.setQuestion(question.trim());
        ch.setSessionId(sessionId);
        ch.setCreatedAt(new Date());
        try {
            String apiUrl = this.env.getProperty("chatbot.api.url");
            String answer = ChatbotClient.ask(apiUrl, question.trim());
            ch.setAnswer(answer);
        } catch (Exception e) {
            System.err.println("ChatbotClient.ask thất bại: " + e.getMessage());
            ch.setAnswer(null);
        }
        this.chatHistoryRepo.saveOrUpdate(ch);
        return ch;
    }

    @Override
    public List<ChatHistory> getMyHistory(long userId, Integer page, Integer size) {
        return this.chatHistoryRepo.getByUser(userId, page, size);
    }

    @Override
    public List<ChatHistory> getBySession(String sessionId) {
        return this.chatHistoryRepo.getBySession(sessionId);
    }

    @Override
    public List<ChatHistory> getPending(User caller, Integer page, Integer size) {
        Long departmentId = null;
        if ("TRAINER".equals(caller.getRole()) && caller.getDepartmentId() != null) {
            departmentId = caller.getDepartmentId().getId();
        }
        return this.chatHistoryRepo.getPending(departmentId, page, size);
    }

    @Override
    public ChatHistory getById(long id) {
        return this.chatHistoryRepo.getById(id);
    }

    @Override
    public ChatHistory answer(long id, String answertext) {
        ChatHistory ch = this.chatHistoryRepo.getById(id);
        if (ch == null)
            throw new IllegalArgumentException("Không tìm thấy câu hỏi");
        if (ch.getAnswer() != null)
            throw new IllegalArgumentException("Câu hỏi này đã được trả lời");
        if (answertext == null || answertext.trim().isEmpty())
            throw new IllegalArgumentException("Câu trả lời không được để trống");
        ch.setAnswer(answertext.trim());
        this.chatHistoryRepo.saveOrUpdate(ch);
        this.notificationService.create(ch.getUserId().getId(), "Câu hỏi đã được trả lời",
                "Câu hỏi \"" + ch.getQuestion() + "\" của bạn đã có người hỗ trợ trả lời");
        return ch;
    }
    
}
