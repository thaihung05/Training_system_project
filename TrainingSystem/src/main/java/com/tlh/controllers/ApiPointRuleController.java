/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.PointRule;
import com.tlh.service.PointRuleService;
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
@RequestMapping("/api/secure/point-rules")
public class ApiPointRuleController {
    @Autowired
    private PointRuleService pointRuleService;
    
    @GetMapping
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(this.pointRuleService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createRule(
            @RequestBody PointRule body) {
        body.setId(null);
        PointRule created = this.pointRuleService.addRule(body);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(
            @PathVariable(value = "id") long id, 
            @RequestBody PointRule body) {
        if (this.pointRuleService.getById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy quy tắc", HttpStatus.NOT_FOUND);
        }
        body.setId(id);
        PointRule updated = this.pointRuleService.updateRule(body);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(
            @PathVariable(value = "id") long id) {
        if (this.pointRuleService.getById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy quy tắc", HttpStatus.NOT_FOUND);
        }
        this.pointRuleService.deleteRule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }   
}
