/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.PointTransaction;
import java.util.List;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public interface PointTransactionService {
    List<PointTransaction> getMyTransactions(long userId);
    long getTotalPoints(long userId);
    long getTotalPointsIssued();
    List<Map<String, Object>> getLeaderboard(int limit);
    void awardPoints(Long userId, String actionType, String reason);
}
