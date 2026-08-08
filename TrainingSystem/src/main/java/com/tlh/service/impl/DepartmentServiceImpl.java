/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Department;
import com.tlh.repository.DepartmentRepository;
import com.tlh.service.DepartmentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepo;
    
    @Override
    public List<Department> getDepartments() {
        return this.departmentRepo.getDepartments();
    }

    @Override
    public Department getDepartmentById(long id) {
        return this.departmentRepo.getDepartmentById(id);
    }

    @Override
    public Department addOrUpdate(Department d) {
        if (d.getName() == null || d.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng ban không được để trống");
        }
        if (d.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên phòng ban tối đa 100 ký tự");
        }
        d.setName(d.getName().trim());

        Department existed = this.departmentRepo.getDepartmentByName(d.getName());
        if (existed != null && !existed.getId().equals(d.getId())) {
            throw new IllegalArgumentException("Tên phòng ban đã tồn tại: " + d.getName());
        }
        this.departmentRepo.saveOrUpdate(d);
        return d;
    }

    @Override
    public void deleteDepartment(long id) {
        this.departmentRepo.deleteDepartment(id);
    }
    
}
