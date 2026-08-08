/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Badge;
import com.tlh.repository.BadgeRepository;
import com.tlh.service.BadgeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class BadgeServiceImpl implements BadgeService{
    
    @Autowired
    private BadgeRepository badgeRepo;

    private void validate(Badge b) {
        if (b.getCode() == null || b.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã huy hiệu không được để trống");
        }
        if (b.getCode().trim().length() > 50) {
            throw new IllegalArgumentException("Mã huy hiệu tối đa 50 ký tự");
        }
        if (b.getName() == null || b.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên huy hiệu không được để trống");
        }
        if (b.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên huy hiệu tối đa 100 ký tự");
        }
        b.setCode(b.getCode().trim());
        b.setName(b.getName().trim());
    }
    
    @Override
    public List<Badge> getAll() {
        return this.badgeRepo.getAll();
    }

    @Override
    public Badge getById(long id) {
        return this.badgeRepo.getById(id);
    }

    @Override
    public Badge getByCode(String code) {
        return this.badgeRepo.getByCode(code);
    }

    @Override
    public Badge addBadge(Badge b) {
        validate(b);
        Badge existed = this.badgeRepo.getByCode(b.getCode());
        if (existed != null) {
            throw new IllegalArgumentException("Mã huy hiệu đã tồn tại: " + b.getCode());
        }
        this.badgeRepo.saveOrUpdate(b);
        return b;
    }

    @Override
    public Badge updateBadge(Badge b) {
        validate(b);
        Badge existed = this.badgeRepo.getByCode(b.getCode());
        if (existed != null && !existed.getId().equals(b.getId())) {
            throw new IllegalArgumentException("Mã huy hiệu đã tồn tại: " + b.getCode());
        }
        this.badgeRepo.saveOrUpdate(b);
        return b;
    }

    @Override
    public void deleteBadge(long id) {
        this.badgeRepo.delete(id);
    }

}
