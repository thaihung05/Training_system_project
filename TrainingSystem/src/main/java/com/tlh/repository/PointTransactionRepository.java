/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.PointTransaction;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface PointTransactionRepository {
    List<PointTransaction> getByUser(long userId);
    long getTotalPoints(long userId);
    long getTotalPointsIssued();
    void saveOrUpdate(PointTransaction t);
}
