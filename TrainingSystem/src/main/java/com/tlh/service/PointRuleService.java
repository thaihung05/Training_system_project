/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.PointRule;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface PointRuleService {
    List<PointRule> getAll();
    PointRule getById(long id);
    PointRule addRule(PointRule r);
    PointRule updateRule(PointRule r);
    void deleteRule(long id);
}
