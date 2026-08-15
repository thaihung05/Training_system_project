/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.User;
import com.tlh.service.UserService;
import com.tlh.utils.JwtUtils;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class ApiAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        User u = this.userService.login(body.get("username"), body.get("password"));
        if (u == null) {
            return new ResponseEntity<>("Sai username hoặc mật khẩu", HttpStatus.UNAUTHORIZED);
        }
        try {
            String token = JwtUtils.generateToken(u.getUsername(), u.getRole());
            return ResponseEntity.ok().body(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tạo JWT", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
