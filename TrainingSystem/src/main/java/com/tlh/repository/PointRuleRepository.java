/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.PointRule;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface PointRuleRepository {
    List<PointRule> getAll();
    PointRule getById(long id);
    PointRule getByActionType(String actionType);
    void saveOrUpdate(PointRule r);
    void delete(long id);
}
