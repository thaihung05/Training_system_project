/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.ChatHistory;
import com.tlh.pojo.User;
import com.tlh.service.ChatHistoryService;
import com.tlh.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@CrossOrigin
@RestController
@RequestMapping("/api/secure/chat-history")
public class ApiChatHistoryController {
    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private UserService userService;

    private User currentUser(Principal principal) {
        return this.userService.getUserByUsername(principal.getName());
    }
    
    @PostMapping("/ask")
    public ResponseEntity<?> ask(
            @RequestBody Map<String, String> body, 
            Principal principal) {
        User caller = currentUser(principal);
        ChatHistory created = this.chatHistoryService.ask(caller, body.get("question"), body.get("sessionId"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> myHistory(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        return new ResponseEntity<>(this.chatHistoryService.getMyHistory(caller.getId(), page, size), HttpStatus.OK);
    }
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> bySession(
            @PathVariable(value = "sessionId") String sessionId, 
            Principal principal) {
        User caller = currentUser(principal);
        List<ChatHistory> list = this.chatHistoryService.getBySession(sessionId);
        for (ChatHistory ch : list) {
            if (!ch.getUserId().getId().equals(caller.getId())) {
                return new ResponseEntity<>("Bạn không có quyền xem phiên hỏi đáp này", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> pending(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Principal principal) {
        User caller = currentUser(principal);
        if (!"TRAINER".equals(caller.getRole()) && !"ADMIN".equals(caller.getRole())) {
            return new ResponseEntity<>("Bạn không có quyền xem danh sách này", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(this.chatHistoryService.getPending(caller, page, size), HttpStatus.OK);
    }
    
    @PutMapping("/{id}/answer")
    public ResponseEntity<?> answer(
            @PathVariable(value = "id") long id,
            @RequestBody Map<String, String> body,
            Principal principal) {
        User caller = currentUser(principal);
        if (!"TRAINER".equals(caller.getRole()) && !"ADMIN".equals(caller.getRole())) {
            return new ResponseEntity<>("Bạn không có quyền trả lời câu hỏi này", HttpStatus.FORBIDDEN);
        }
        ChatHistory updated = this.chatHistoryService.answer(id, caller, body.get("answer"));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}