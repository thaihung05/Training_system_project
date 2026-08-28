/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Notification;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface NotificationService {
    List<Notification> getMyNotifications(long userId, boolean unreadOnly, Integer page, Integer size);
    long countUnread(long userId);
    Notification getById(long id);
    Notification markRead(Notification n);
    int markAllRead(long userId);
    void delete(long id);
    void create(Long userId, String title, String content, String link);
}
