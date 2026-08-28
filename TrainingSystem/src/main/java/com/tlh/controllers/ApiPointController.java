/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.User;
import com.tlh.service.PointTransactionService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure/points")
public class ApiPointController {
    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private UserService userService;
    
    private User currentUser(Principal principal){
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> myPoints(Principal principal) {
        User caller = currentUser(principal);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactions", this.pointTransactionService.getMyTransactions(caller.getId()));
        result.put("totalPoints", this.pointTransactionService.getTotalPoints(caller.getId()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard(
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return new ResponseEntity<>(this.pointTransactionService.getLeaderboard(limit), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> userPoints(@PathVariable(value = "userId") long userId, Principal principal) {
        User caller = currentUser(principal);
        if (!"ADMIN".equals(caller.getRole())) {
            return new ResponseEntity<>("Bạn không có quyền xem điểm của người khác", HttpStatus.FORBIDDEN);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactions", this.pointTransactionService.getMyTransactions(userId));
        result.put("totalPoints", this.pointTransactionService.getTotalPoints(userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/total-issued")
    public ResponseEntity<?> totalIssued() {
        return new ResponseEntity<>(Collections.singletonMap("totalPointsIssued", this.pointTransactionService.getTotalPointsIssued()), HttpStatus.OK);
    }

}
