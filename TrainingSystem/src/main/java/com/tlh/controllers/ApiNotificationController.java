/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Notification;
import com.tlh.pojo.User;
import com.tlh.service.NotificationService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@RestController
@CrossOrigin
@RequestMapping("/api/secure/notifications")
public class ApiNotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    private User currentUser(Principal principal){
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @GetMapping
    public ResponseEntity<?> getMyNotifications(
            @RequestParam(value = "unreadOnly", required = false, defaultValue = "false") boolean unreadOnly,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal){
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.notificationService.getMyNotifications(caller.getId(), unreadOnly, page, size), HttpStatus.OK);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount(Principal principal){
        User caller = currentUser(principal);
        long count = this.notificationService.countUnread(caller.getId());
        return new ResponseEntity<>(Collections.singletonMap("count", count), HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable(value = "id") long id,
            Principal principal){
        Notification n = this.notificationService.getById(id);
        if (n == null) 
            return new ResponseEntity<>("Không tìm thấy thông báo", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!n.getUserId().getId().equals(caller.getId()))
            return new ResponseEntity<>("Bạn không có quyền với thông báo này", HttpStatus.FORBIDDEN);
        Notification updated = this.notificationService.markRead(n);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead(Principal principal){
        User caller = currentUser(principal);
        int count = this.notificationService.markAllRead(caller.getId());
        return new ResponseEntity<>(Collections.singletonMap("updated", count), HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") long id, 
            Principal principal){
        Notification n = this.notificationService.getById(id);
        if (n==null)
            return new ResponseEntity<>("Không tìm thấy thông báo", HttpStatus.NOT_FOUND);
        User caller = currentUser(principal);
        if (!n.getUserId().getId().equals(caller.getId()))
            return new ResponseEntity<>("Bạn không có quyền với thông báo này", HttpStatus.FORBIDDEN);
        this.notificationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
