/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Store;
import com.tlh.service.StoreService;
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
@RequestMapping("/api/secure/stores")
public class ApiStoreController {
    @Autowired
    private StoreService storeService;

    @GetMapping
    public ResponseEntity<?> getStores() {
        return new ResponseEntity<>(this.storeService.getStores(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable(value = "id") long id) {
        Store s = this.storeService.getStoreById(id);
        if (s == null) {
            return new ResponseEntity<>("Không tìm thấy siêu thị", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addStore(@RequestBody Store s) {
        s.setId(null);
        return new ResponseEntity<>(this.storeService.addOrUpdate(s), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(
            @PathVariable(value = "id") long id,
            @RequestBody Store s) {
        if (this.storeService.getStoreById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy siêu thị", HttpStatus.NOT_FOUND);
        }
        s.setId(id);
        return new ResponseEntity<>(this.storeService.addOrUpdate(s), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStore(@PathVariable(value = "id") long id) {
        if (this.storeService.getStoreById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy siêu thị", HttpStatus.NOT_FOUND);
        }
        this.storeService.deleteStore(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
