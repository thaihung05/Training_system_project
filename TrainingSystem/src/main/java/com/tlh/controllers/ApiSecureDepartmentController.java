/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.controllers;

import com.tlh.pojo.Department;
import com.tlh.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/secure/departments")
public class ApiSecureDepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<?> addDepartment(@RequestBody Department d) {
        d.setId(null);
        return new ResponseEntity<>(this.departmentService.addOrUpdate(d), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable(value = "id") long id,
            @RequestBody Department d) {
        if (this.departmentService.getDepartmentById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy phòng ban", HttpStatus.NOT_FOUND);
        }
        d.setId(id);
        return new ResponseEntity<>(this.departmentService.addOrUpdate(d), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable(value = "id") long id) {
        if (this.departmentService.getDepartmentById(id) == null) {
            return new ResponseEntity<>("Không tìm thấy phòng ban", HttpStatus.NOT_FOUND);
        }
        this.departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
