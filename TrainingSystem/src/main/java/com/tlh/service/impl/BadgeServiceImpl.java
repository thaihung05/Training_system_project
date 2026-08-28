/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Badge;
import com.tlh.repository.BadgeRepository;
import com.tlh.service.BadgeService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class BadgeServiceImpl implements BadgeService{

    private static final Set<String> SYSTEM_CODES = Set.of(
            "CERT_1",
            "CERT_5",
            "CERT_10"
    );

    @Autowired
    private BadgeRepository badgeRepo;

    private void validate(Badge b) {
        if (b.getCode() == null || b.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã huy hiệu không được để trống");
        }
        String code = b.getCode().trim().toUpperCase();
        if (code.length() > 50) {
            throw new IllegalArgumentException("Mã huy hiệu tối đa 50 ký tự");
        }
        if (!SYSTEM_CODES.contains(code)) {
            throw new IllegalArgumentException("Mã huy hiệu không thuộc danh mục huy hiệu của hệ thống");
        }
        if (b.getName() == null || b.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên huy hiệu không được để trống");
        }
        if (b.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên huy hiệu tối đa 100 ký tự");
        }
        b.setCode(code);
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
        if (b.getId() == null) {
            throw new IllegalArgumentException("Không tìm thấy huy hiệu");
        }
        Badge current = this.badgeRepo.getById(b.getId());
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy huy hiệu");
        }
        if (SYSTEM_CODES.contains(current.getCode()) && !current.getCode().equals(b.getCode())) {
            throw new IllegalArgumentException("Không thể thay đổi mã của huy hiệu hệ thống");
        }
        Badge existed = this.badgeRepo.getByCode(b.getCode());
        if (existed != null && !existed.getId().equals(b.getId())) {
            throw new IllegalArgumentException("Mã huy hiệu đã tồn tại: " + b.getCode());
        }
        this.badgeRepo.saveOrUpdate(b);
        return b;
    }

    @Override
    public void deleteBadge(long id) {
        Badge current = this.badgeRepo.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy huy hiệu");
        }
        if (SYSTEM_CODES.contains(current.getCode())) {
            throw new IllegalArgumentException("Không thể xóa huy hiệu hệ thống");
        }
        this.badgeRepo.delete(id);
    }

}
