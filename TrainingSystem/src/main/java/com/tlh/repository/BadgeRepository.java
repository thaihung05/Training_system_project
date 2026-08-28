/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Badge;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface BadgeRepository {
    List<Badge> getAll();
    Badge getById(long id);
    Badge getByCode(String code);
    void saveOrUpdate(Badge b);
    void delete(long id);
}
