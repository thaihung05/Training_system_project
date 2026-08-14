/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.PointRule;
import com.tlh.pojo.PointTransaction;
import com.tlh.pojo.User;
import com.tlh.repository.PointRuleRepository;
import com.tlh.repository.PointTransactionRepository;
import com.tlh.service.PointTransactionService;
import com.tlh.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class PointTransactionServiceImpl implements PointTransactionService{

    @Autowired
    private PointTransactionRepository pointTransactionRepo;

    @Autowired
    private PointRuleRepository pointRuleRepo;

    @Autowired
    private UserService userService;


    @Override
    public List<PointTransaction> getMyTransactions(long userId) {
        return this.pointTransactionRepo.getByUser(userId);
    }

    @Override
    public long getTotalPoints(long userId) {
        return this.pointTransactionRepo.getTotalPoints(userId);
    }

    @Override
    public long getTotalPointsIssued() {
        return this.pointTransactionRepo.getTotalPointsIssued();
    }

    @Override
    public List<Map<String, Object>> getLeaderboard(int limit) {
        List<User> users = this.userService.getUsers(new HashMap<>());
        List<Map<String, Object>> all = new ArrayList<>();
        for (User u : users) {
            if (!"EMPLOYEE".equals(u.getRole())) {
                continue;
            }
            long total = this.pointTransactionRepo.getTotalPoints(u.getId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("userId", u.getId());
            item.put("name", u.getName());
            item.put("totalPoints", total);
            all.add(item);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        while (!all.isEmpty() && result.size() < limit) {
            Map<String, Object> topItem = all.get(0);
            for (Map<String, Object> item : all) {
                long itemPoints = (Long) item.get("totalPoints");
                long topPoints = (Long) topItem.get("totalPoints");
                if (itemPoints > topPoints) {
                    topItem = item;
                }
            }
            result.add(topItem);
            all.remove(topItem);
        }
        return result;
    }

    @Override
    public void awardPoints(Long userId, String actionType, String reason) {
        if (userId == null || actionType == null) {
            return;
        }
        PointRule rule = this.pointRuleRepo.getByActionType(actionType);
        if (rule == null) {
            System.err.println("Khong co PointRule cho actionType: " + actionType);
            return;
        }
        PointTransaction t = new PointTransaction();
        t.setUserId(new User(userId));
        t.setRuleId(rule);
        t.setPoints(rule.getPoints());
        t.setReason(reason);
        this.pointTransactionRepo.saveOrUpdate(t);
    }
    
}
