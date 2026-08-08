/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service;

import com.tlh.pojo.Department;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public interface DepartmentService {
    List<Department> getDepartments();
    Department getDepartmentById(long id);
    Department addOrUpdate(Department d);
    void deleteDepartment(long id);
}
