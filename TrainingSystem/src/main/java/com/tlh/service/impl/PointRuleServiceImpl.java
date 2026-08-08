/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.PointRule;
import com.tlh.repository.PointRuleRepository;
import com.tlh.service.PointRuleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class PointRuleServiceImpl implements PointRuleService{
    
    @Autowired
    private PointRuleRepository pointRuleRepo;

    private void validate(PointRule r){
        if (r.getActionType() == null || r.getActionType().trim().isEmpty()){
            throw new IllegalArgumentException("Loại hành động không được để trống");
        }
        r.setActionType(r.getActionType().trim());
    }

    @Override
    public List<PointRule> getAll() {
        return this.pointRuleRepo.getAll();
    }

    @Override
    public PointRule getById(long id) {
        return this.pointRuleRepo.getById(id);
    }

    @Override
    public PointRule addRule(PointRule r) {
        validate(r);
        PointRule existed = this.pointRuleRepo.getByActionType(r.getActionType());
        if (existed != null) {
            throw new IllegalArgumentException("Quy tắc cho hành động này đã tồn tại");
        }
        this.pointRuleRepo.saveOrUpdate(r);
        return r;
    }

    @Override
    public PointRule updateRule(PointRule r) {
        validate(r);
        PointRule existed = this.pointRuleRepo.getByActionType(r.getActionType());
        if (existed != null && !existed.getId().equals(r.getId())) {
            throw new IllegalArgumentException("Quy tắc cho hành động này đã tồn tại");
        }
        this.pointRuleRepo.saveOrUpdate(r);
        return r;
    }

    @Override
    public void deleteRule(long id) {
        this.pointRuleRepo.delete(id);
    }
    
}
