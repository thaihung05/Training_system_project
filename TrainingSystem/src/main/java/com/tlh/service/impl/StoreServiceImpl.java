/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Store;
import com.tlh.repository.StoreRepository;
import com.tlh.repository.UserRepository;
import com.tlh.service.ChainService;
import com.tlh.service.RegionService;
import com.tlh.service.StoreService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class StoreServiceImpl implements StoreService{

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChainService chainService;

    @Autowired
    private RegionService regionService;

    @Override
    public List<Store> getStores() {
        return this.storeRepo.getStores();
    }

    @Override
    public Store getStoreById(long id) {
        return this.storeRepo.getStoreById(id);
    }

    @Override
    public Store addOrUpdate(Store s) {
        if (s.getName() == null || s.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên siêu thị không được để trống");
        }
        if (s.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên siêu thị tối đa 100 ký tự");
        }
        s.setName(s.getName().trim());

        if (s.getMaSt() == null || !s.getMaSt().trim().matches("\\d{4}")) {
            throw new IllegalArgumentException("Mã siêu thị phải gồm đúng 4 chữ số");
        }
        s.setMaSt(s.getMaSt().trim());

        if (s.getChainId() == null || this.chainService.getChainById(s.getChainId().getId()) == null) {
            throw new IllegalArgumentException("Chuỗi không tồn tại");
        }
        if (s.getRegionId() == null || this.regionService.getRegionById(s.getRegionId().getId()) == null) {
            throw new IllegalArgumentException("Vùng không tồn tại");
        }

        Store existed = this.storeRepo.getStoreByName(s.getName());
        if (existed != null && !existed.getId().equals(s.getId())) {
            throw new IllegalArgumentException("Tên siêu thị đã tồn tại: " + s.getName());
        }
        Store existedMaSt = this.storeRepo.getStoreByMaSt(s.getMaSt());
        if (existedMaSt != null && !existedMaSt.getId().equals(s.getId())) {
            throw new IllegalArgumentException("Mã siêu thị đã tồn tại: " + s.getMaSt());
        }
        this.storeRepo.saveOrUpdate(s);
        return s;
    }

    @Override
    public void deleteStore(long id) {
        Store existing = this.storeRepo.getStoreById(id);
        if (existing == null) {
            return;
        }
        long userCount = this.userRepo.countByStore(id);
        if (userCount > 0) {
            throw new IllegalArgumentException("Không thể xóa siêu thị \"" + existing.getName()
                    + "\" vì còn " + userCount + " người dùng đang gắn với siêu thị này.");
        }
        this.storeRepo.deleteStore(id);
    }

}
