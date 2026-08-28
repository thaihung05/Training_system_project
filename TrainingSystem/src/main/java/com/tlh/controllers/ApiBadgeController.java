/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Badge;
import com.tlh.pojo.User;
import com.tlh.service.BadgeService;
import com.tlh.service.UserBadgeService;
import com.tlh.service.UserService;
import java.security.Principal;
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
@RequestMapping("/api")
public class ApiBadgeController {
    
    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserBadgeService userBadgeService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @GetMapping("/badges")
    public ResponseEntity<?> getCatalog(){
        return new ResponseEntity<>(this.badgeService.getAll(), HttpStatus.OK);
    }
    
    @PostMapping("/secure/badges")
    public ResponseEntity<?> createBadge(@RequestBody Badge body) {
        body.setId(null);
        Badge created = this.badgeService.addBadge(body);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/secure/badges/{id}")
    public ResponseEntity<?> updateBadge(
            @PathVariable(value = "id") long id, 
            @RequestBody Badge body) {
        if (this.badgeService.getById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy huy hiệu", HttpStatus.NOT_FOUND);
        }
        
        body.setId(id);
        Badge updated = this.badgeService.updateBadge(body);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @DeleteMapping("/secure/badges/{id}")
    public ResponseEntity<?> deleteBadge(@PathVariable(value = "id") long id) {
        if (this.badgeService.getById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy huy hiệu", HttpStatus.NOT_FOUND);
        }
        this.badgeService.deleteBadge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/secure/badges/my")
    public ResponseEntity<?> myBadges(Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.userBadgeService.getMyBadges(caller.getId()), HttpStatus.OK);
    }
}
