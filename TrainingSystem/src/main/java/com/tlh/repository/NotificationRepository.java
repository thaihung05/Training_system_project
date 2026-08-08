/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Notification;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface NotificationRepository {
    List<Notification> getByUser(long userId, boolean unreadOnly, Integer page, Integer size);
    Notification getById(long id);
    void saveOrUpdate(Notification n);
    int markAllRead(long userId);
    void delete(long id);
}
