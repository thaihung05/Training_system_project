/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Notification;
import com.tlh.pojo.User;
import com.tlh.repository.NotificationRepository;
import com.tlh.service.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class NotificationServiceImpl implements NotificationService{

    @Autowired
    private NotificationRepository notificationRepo;
    
    @Override
    public List<Notification> getMyNotifications(long userId, boolean unreadOnly, Integer page, Integer size) {
        return this.notificationRepo.getByUser(userId, unreadOnly, page, size);
    }

    @Override
    public Notification getById(long id) {
        return this.notificationRepo.getById(id);
    }

    @Override
    public Notification markRead(Notification n) {
        n.setIsRead(true);
        this.notificationRepo.saveOrUpdate(n);
        return n;
    }

    @Override
    public int markAllRead(long userId) {
        return this.notificationRepo.markAllRead(userId);
    }

    @Override
    public void delete(long id) {
        this.notificationRepo.delete(id);
    }

    @Override
    public void create(Long userId, String title, String content) {
        try {
            if (userId == null)
                return;
            Notification n = new Notification();
            n.setUserId(new User(userId));
            n.setTitle(title);
            n.setContent(content);
            n.setIsRead(false);
            this.notificationRepo.saveOrUpdate(n);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
}
