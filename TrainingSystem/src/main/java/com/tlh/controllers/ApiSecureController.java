/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.User;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/secure")
public class ApiSecureController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        User u = this.userService.getUserByUsername(principal.getName());
        if (u == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody Map<String, String> body) {
        User u = this.userService.getUserByUsername(principal.getName());
        if (u == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.userService.changePassword(u.getId(), body.get("oldPassword"), body.get("newPassword"));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
