/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.UserBadge;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface UserBadgeRepository {
    List<UserBadge> getByUser(long userId);
    UserBadge getByUserAndBadge(long userId, long badgeId);
    void saveOrUpdate(UserBadge b);
}
