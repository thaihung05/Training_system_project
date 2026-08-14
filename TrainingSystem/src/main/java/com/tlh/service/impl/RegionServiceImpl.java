/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Region;
import com.tlh.repository.RegionRepository;
import com.tlh.service.RegionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class RegionServiceImpl implements RegionService{

    @Autowired
    private RegionRepository regionRepo;

    @Override
    public List<Region> getRegions() {
        return this.regionRepo.getRegions();
    }

    @Override
    public Region getRegionById(long id) {
        return this.regionRepo.getRegionById(id);
    }

    @Override
    public Region addOrUpdate(Region r) {
        if (r.getName() == null || r.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên vùng không được để trống");
        }
        if (r.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên vùng tối đa 100 ký tự");
        }
        r.setName(r.getName().trim());

        Region existed = this.regionRepo.getRegionByName(r.getName());
        if (existed != null && !existed.getId().equals(r.getId())) {
            throw new IllegalArgumentException("Tên vùng đã tồn tại: " + r.getName());
        }
        this.regionRepo.saveOrUpdate(r);
        return r;
    }

    @Override
    public void deleteRegion(long id) {
        this.regionRepo.deleteRegion(id);
    }

}
