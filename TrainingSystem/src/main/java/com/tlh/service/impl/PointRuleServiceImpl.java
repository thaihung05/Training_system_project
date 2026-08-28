/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.PointRule;
import com.tlh.repository.PointRuleRepository;
import com.tlh.service.PointRuleService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class PointRuleServiceImpl implements PointRuleService{

    private static final Set<String> SYSTEM_ACTION_TYPES = Set.of(
            "LESSON_COMPLETED",
            "TEST_PASSED",
            "COURSE_COMPLETED"
    );
    
    @Autowired
    private PointRuleRepository pointRuleRepo;

    private void validate(PointRule r){
        if (r == null) {
            throw new IllegalArgumentException("Dữ liệu quy tắc không hợp lệ");
        }
        if (r.getActionType() == null || r.getActionType().trim().isEmpty()){
            throw new IllegalArgumentException("Loại hành động không được để trống");
        }
        String actionType = r.getActionType().trim().toUpperCase();
        if (actionType.length() > 50) {
            throw new IllegalArgumentException("Loại hành động tối đa 50 ký tự");
        }
        if (!SYSTEM_ACTION_TYPES.contains(actionType)) {
            throw new IllegalArgumentException("Loại hành động không thuộc quy tắc điểm của hệ thống");
        }
        if (r.getPoints() < 0) {
            throw new IllegalArgumentException("Số điểm không được nhỏ hơn 0");
        }
        if (r.getDescription() != null) {
            String description = r.getDescription().trim();
            if (description.length() > 255) {
                throw new IllegalArgumentException("Mô tả tối đa 255 ký tự");
            }
            r.setDescription(description.isEmpty() ? null : description);
        }
        r.setActionType(actionType);
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
        if (r.getId() == null) {
            throw new IllegalArgumentException("Không tìm thấy quy tắc");
        }
        PointRule current = this.pointRuleRepo.getById(r.getId());
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy quy tắc");
        }
        if (!current.getActionType().equals(r.getActionType())) {
            throw new IllegalArgumentException("Không thể thay đổi mã hành động của quy tắc hệ thống");
        }
        this.pointRuleRepo.saveOrUpdate(r);
        return r;
    }

    @Override
    public void deleteRule(long id) {
        PointRule current = this.pointRuleRepo.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy quy tắc");
        }
        if (SYSTEM_ACTION_TYPES.contains(current.getActionType())) {
            throw new IllegalArgumentException("Không thể xóa quy tắc điểm của hệ thống");
        }
        this.pointRuleRepo.delete(id);
    }
    
}
