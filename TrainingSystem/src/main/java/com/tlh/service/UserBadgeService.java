/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.UserBadge;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface UserBadgeService {
    List<UserBadge> getMyBadges(long userId);
    void checkAndAward(Long userId, String badgeCode);
}
