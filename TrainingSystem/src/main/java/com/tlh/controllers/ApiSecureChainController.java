/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Chain;
import com.tlh.service.ChainService;
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
@RequestMapping("/api/secure/chains")
public class ApiSecureChainController {
    @Autowired
    private ChainService chainService;

    @GetMapping
    public ResponseEntity<?> getChains() {
        return new ResponseEntity<>(this.chainService.getChains(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChainById(@PathVariable(value = "id") long id) {
        Chain c = this.chainService.getChainById(id);
        if (c == null) {
            return new ResponseEntity<>("Không tìm thấy chuỗi", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addChain(@RequestBody Chain c) {
        c.setId(null);
        return new ResponseEntity<>(this.chainService.addOrUpdate(c), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChain(
            @PathVariable(value = "id") long id,
            @RequestBody Chain c) {
        if (this.chainService.getChainById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy chuỗi", HttpStatus.NOT_FOUND);
        }
        c.setId(id);
        return new ResponseEntity<>(this.chainService.addOrUpdate(c), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChain(@PathVariable(value = "id") long id) {
        if (this.chainService.getChainById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy chuỗi", HttpStatus.NOT_FOUND);
        }
        this.chainService.deleteChain(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
