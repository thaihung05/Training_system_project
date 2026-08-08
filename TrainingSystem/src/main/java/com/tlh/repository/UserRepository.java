/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tlh.repository;

import com.tlh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author LENOVO
 */
public interface UserRepository {
    List<User> getUsers(Map<String, String> params);
    long countUsers(String kw);
    User getUserById(long id);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    List<User> getUserByRole(String role);
    List<User> getUserByDepartment(String departmentId);
    void saveOrUpdate(User u);
    void deactivateUser(long id);
    boolean authenticate(String username, String rawPassword);
}
