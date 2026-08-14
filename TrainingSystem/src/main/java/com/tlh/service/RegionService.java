/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Region;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface RegionService {
    List<Region> getRegions();
    Region getRegionById(long id);
    Region addOrUpdate(Region r);
    void deleteRegion(long id);
}
