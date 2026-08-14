/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Store;
import com.tlh.repository.StoreRepository;
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
        this.storeRepo.saveOrUpdate(s);
        return s;
    }

    @Override
    public void deleteStore(long id) {
        this.storeRepo.deleteStore(id);
    }

}
