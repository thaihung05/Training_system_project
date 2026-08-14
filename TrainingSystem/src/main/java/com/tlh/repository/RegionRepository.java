/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.Region;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface RegionRepository {
    List<Region> getRegions();
    Region getRegionById(long id);
    Region getRegionByName(String name);
    void saveOrUpdate(Region r);
    void deleteRegion(long id);
}
