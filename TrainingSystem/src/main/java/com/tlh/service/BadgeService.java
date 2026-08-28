/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Badge;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface BadgeService {
    List<Badge> getAll();
    Badge getById(long id);
    Badge getByCode(String code);
    Badge addBadge(Badge b);
    Badge updateBadge(Badge b);
    void deleteBadge(long id);
}
