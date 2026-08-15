/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Chain;
import com.tlh.repository.ChainRepository;
import com.tlh.repository.CourseRepository;
import com.tlh.repository.StoreRepository;
import com.tlh.service.ChainService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class ChainServiceImpl implements ChainService{

    @Autowired
    private ChainRepository chainRepo;

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Override
    public List<Chain> getChains() {
        return this.chainRepo.getChains();
    }

    @Override
    public Chain getChainById(long id) {
        return this.chainRepo.getChainById(id);
    }

    @Override
    public Chain addOrUpdate(Chain c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuỗi không được để trống");
        }
        if (c.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên chuỗi tối đa 100 ký tự");
        }
        c.setName(c.getName().trim());

        Chain existed = this.chainRepo.getChainByName(c.getName());
        if (existed != null && !existed.getId().equals(c.getId())) {
            throw new IllegalArgumentException("Tên chuỗi đã tồn tại: " + c.getName());
        }
        this.chainRepo.saveOrUpdate(c);
        return c;
    }

    @Override
    public void deleteChain(long id) {
        Chain c = this.chainRepo.getChainById(id);
        if (c == null) {
            return;
        }
        long storeCount = this.storeRepo.countByChain(id);
        long courseCount = this.courseRepo.countByChain(id);
        if (storeCount > 0 || courseCount > 0) {
            StringBuilder msg = new StringBuilder("Không thể xóa chuỗi \"" + c.getName() + "\" vì còn");
            if (storeCount > 0) {
                msg.append(" ").append(storeCount).append(" siêu thị");
            }
            if (storeCount > 0 && courseCount > 0) {
                msg.append(" và");
            }
            if (courseCount > 0) {
                msg.append(" ").append(courseCount).append(" khóa học");
            }
            msg.append(" đang sử dụng.");
            throw new IllegalArgumentException(msg.toString());
        }
        this.chainRepo.deleteChain(id);
    }

}
