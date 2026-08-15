/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Region;
import com.tlh.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure/regions")
public class ApiRegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<?> getRegions() {
        return new ResponseEntity<>(this.regionService.getRegions(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRegionById(@PathVariable(value = "id") long id) {
        Region r = this.regionService.getRegionById(id);
        if (r == null) {
            return new ResponseEntity<>("Không tìm thấy vùng", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addRegion(@RequestBody Region r) {
        r.setId(null);
        return new ResponseEntity<>(this.regionService.addOrUpdate(r), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegion(
            @PathVariable(value = "id") long id,
            @RequestBody Region r) {
        if (this.regionService.getRegionById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy vùng", HttpStatus.NOT_FOUND);
        }
        r.setId(id);
        return new ResponseEntity<>(this.regionService.addOrUpdate(r), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable(value = "id") long id) {
        if (this.regionService.getRegionById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy vùng", HttpStatus.NOT_FOUND);
        }
        this.regionService.deleteRegion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
