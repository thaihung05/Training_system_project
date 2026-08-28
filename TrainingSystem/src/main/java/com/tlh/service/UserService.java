/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
public interface UserService {
    List<User> getUsers(Map<String, String> params);
    long countUsers(String kw);
    User getUserById(long id);
    User getUserByUsername(String username);
    List<User> getUsersByRole(String role);
    List<User> getUsersByStore(String storeId);
    User createUser(User u);
    List<Map<String, Object>> bulkImportUsers(MultipartFile file);
    User login(String username, String rawPassword);
    void deactivateUser(long id);
    void reactivateUser(long id);
    User updateUser(long id, User body);
    void changePassword(long userId, String oldPassword, String newPassword);
}
