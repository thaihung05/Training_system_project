/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Region;
import com.tlh.repository.CourseRepository;
import com.tlh.repository.RegionRepository;
import com.tlh.repository.StoreRepository;
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

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private CourseRepository courseRepo;

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
        Region r = this.regionRepo.getRegionById(id);
        if (r == null) {
            return;
        }
        long storeCount = this.storeRepo.countByRegion(id);
        long courseCount = this.courseRepo.countByRegion(id);
        if (storeCount > 0 || courseCount > 0) {
            StringBuilder msg = new StringBuilder("Không thể xóa vùng \"" + r.getName() + "\" vì còn");
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
        this.regionRepo.deleteRegion(id);
    }

}
