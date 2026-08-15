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
import com.tlh.service.UserService;
import com.tlh.utils.ChatbotClient;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class ChatHistoryServiceImpl implements ChatHistoryService{

    @Autowired
    private ChatHistoryRepository chatHistoryRepo;

    @Autowired
    private Environment env;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public ChatHistory ask(User caller, String question, String sessionId) {
        if (question == null || question.trim().isEmpty())
            throw new IllegalArgumentException("Câu hỏi không được để trống");
        if (question.trim().length() > 65535)
            throw new IllegalArgumentException("Câu hỏi quá dài");
        if (sessionId != null && sessionId.trim().length() > 100)
            throw new IllegalArgumentException("Mã phiên hỏi đáp quá dài");

        ChatHistory ch = new ChatHistory();
        ch.setUserId(caller);
        ch.setQuestion(question.trim());
        ch.setSessionId(sessionId == null ? null : sessionId.trim());
        ch.setCreatedAt(new Date());
        try {
            String apiUrl = this.env.getProperty("chatbot.api.url");
            String answer = ChatbotClient.ask(apiUrl, question.trim());
            ch.setAnswer(isNotFoundAnswer(answer) ? null : answer);
        } catch (Exception e) {
            System.err.println("ChatbotClient.ask thất bại: " + e.getMessage());
            ch.setAnswer(null);
        }
        this.chatHistoryRepo.saveOrUpdate(ch);
        if (ch.getAnswer() == null) {
            notifyTrainersOfPendingQuestion(caller);
        }
        return ch;
    }

    private boolean isNotFoundAnswer(String answer) {
        if (answer == null) {
            return true;
        }
        String normalized = answer.toLowerCase();
        return normalized.contains("không tìm thấy thông tin");
    }

    private void notifyTrainersOfPendingQuestion(User asker) {
        Long askerStoreId = asker.getStoreId() != null ? asker.getStoreId().getId() : null;
        List<User> trainers = this.userService.getUsersByRole("TRAINER");
        for (User t : trainers) {
            if (!t.getIsActive()) {
                continue;
            }
            boolean companyWide = t.getStoreId() == null;
            boolean sameStore = askerStoreId != null && t.getStoreId() != null
                    && t.getStoreId().getId().equals(askerStoreId);
            if (companyWide || sameStore) {
                this.notificationService.create(t.getId(), "Có câu hỏi mới cần trả lời",
                        asker.getName() + " vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời", "/chat/queue");
            }
        }
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
        Long storeId = null;
        if ("TRAINER".equals(caller.getRole()) && caller.getStoreId() != null) {
            storeId = caller.getStoreId().getId();
        }
        return this.chatHistoryRepo.getPending(storeId, page, size);
    }

    @Override
    public List<ChatHistory> getAnswered(User caller, Integer page, Integer size) {
        Long storeId = null;
        if ("TRAINER".equals(caller.getRole()) && caller.getStoreId() != null) {
            storeId = caller.getStoreId().getId();
        }
        return this.chatHistoryRepo.getAnswered(storeId, page, size);
    }

    @Override
    public ChatHistory getById(long id) {
        return this.chatHistoryRepo.getById(id);
    }

    @Override
    public ChatHistory answer(long id, User caller, String answertext) {
        ChatHistory ch = this.chatHistoryRepo.getById(id);
        if (ch == null)
            throw new IllegalArgumentException("Không tìm thấy câu hỏi");
        if (ch.getAnswer() != null)
            throw new IllegalArgumentException("Câu hỏi này đã được trả lời");
        if (answertext == null || answertext.trim().isEmpty())
            throw new IllegalArgumentException("Câu trả lời không được để trống");
        if (answertext.trim().length() > 65535)
            throw new IllegalArgumentException("Câu trả lời quá dài");
        if (!canAnswer(caller, ch)) {
            throw new IllegalArgumentException("Bạn không có quyền trả lời câu hỏi này");
        }
        ch.setAnswer(answertext.trim());
        this.chatHistoryRepo.saveOrUpdate(ch);
        this.notificationService.create(ch.getUserId().getId(), "Câu hỏi đã được trả lời",
                "Câu hỏi \"" + ch.getQuestion() + "\" của bạn đã có người hỗ trợ trả lời", "/my-questions");
        return ch;
    }

    private boolean canAnswer(User caller, ChatHistory ch) {
        if ("ADMIN".equals(caller.getRole())) {
            return true;
        }
        if (!"TRAINER".equals(caller.getRole())) {
            return false;
        }
        if (caller.getStoreId() == null) {
            return true;
        }
        User asker = ch.getUserId();
        return asker.getStoreId() != null
                && asker.getStoreId().getId().equals(caller.getStoreId().getId());
    }

}
