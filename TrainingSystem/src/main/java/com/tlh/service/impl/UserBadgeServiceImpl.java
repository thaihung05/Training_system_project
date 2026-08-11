/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Badge;
import com.tlh.pojo.User;
import com.tlh.pojo.UserBadge;
import com.tlh.repository.BadgeRepository;
import com.tlh.repository.UserBadgeRepository;
import com.tlh.service.NotificationService;
import com.tlh.service.UserBadgeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class UserBadgeServiceImpl implements UserBadgeService{

    @Autowired
    private UserBadgeRepository userBadgeRepo;

    @Autowired
    private BadgeRepository badgeRepo;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<UserBadge> getMyBadges(long userId) {
        return this.userBadgeRepo.getByUser(userId);
    }

    @Override
    public void checkAndAward(Long userId, String badgeCode) {
         try {
            if (userId == null || badgeCode == null) {
                return;
            }
             Badge badge = this.badgeRepo.getByCode(badgeCode);
            if (badge == null) {
                System.err.println("Khong co Badge voi code: " + badgeCode);
                return;
            }
            UserBadge existed = this.userBadgeRepo.getByUserAndBadge(userId, badge.getId());
            if (existed != null) {
                return;
            }
            UserBadge ub = new UserBadge();
            ub.setUserId(new User(userId));
            ub.setBadgeId(badge);
            this.userBadgeRepo.saveOrUpdate(ub);
            this.notificationService.create(userId, "Huy hiệu mới", "Bạn vừa nhận được huy hiệu " + badge.getName(), "/leaderboard");
        } catch (Exception e) {
            System.err.println("UserBadgeService.checkAndAward that bai: " + e.getMessage());
        }
    }
    
}
