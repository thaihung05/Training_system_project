/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Department;
import com.tlh.pojo.User;
import com.tlh.repository.UserRepository;
import com.tlh.service.DepartmentService;
import com.tlh.service.MailService;
import com.tlh.service.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author LENOVO
 */
@Service
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getUsers(Map<String, String> params) {
        return this.userRepo.getUsers(params);
    }

    @Override
    public long countUsers(String kw) {
        return this.userRepo.countUsers(kw);
    }

    @Override
    public User getUserById(long id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return this.userRepo.getUserByRole(role);
    }

    @Override
    public List<User> getUsersByDepartment(String departmentId) {
        return this.userRepo.getUserByDepartment(departmentId);
    }

    @Override
    public User createUser(User u) {
        if (u.getName() == null || u.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (u.getUsername() == null || u.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (u.getEmail() == null || u.getEmail().trim().isEmpty() || !u.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (u.getPassword() == null || u.getPassword().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (u.getRole() == null || u.getRole().trim().isEmpty()) {
            u.setRole("EMPLOYEE");
        } else if (!List.of("EMPLOYEE", "TRAINER").contains(u.getRole())) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + u.getRole());
        }
        if (u.getDepartmentId() != null && u.getDepartmentId().getId() != null
                && this.departmentService.getDepartmentById(u.getDepartmentId().getId()) == null) {
            throw new IllegalArgumentException("Phòng ban không tồn tại");
        }

        u.setEmail(u.getEmail().trim());
        u.setName(u.getName().trim());
        u.setUsername(u.getUsername().trim());

        if (this.userRepo.getUserByUsername(u.getUsername()) != null) {
            throw new IllegalArgumentException("Username đã được sử dụng: " + u.getUsername());
        }
        User existed = this.userRepo.getUserByEmail(u.getEmail());
        if (existed != null) {
            throw new IllegalArgumentException("Email đã được sử dụng: " + u.getEmail());
        }
        String rawPassword = u.getPassword();
        this.userRepo.saveOrUpdate(u);
        this.mailService.sendAccountCreatedEmail(u, rawPassword);
        return u;
    }

    @Override
    public List<Map<String, Object>> bulkImportUsers(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn file Excel để nhập");
        }
        List<Map<String, Object>> results = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String name = getCellString(formatter, row, 0);
                String username = getCellString(formatter, row, 1);
                String email = getCellString(formatter, row, 2);
                String role = getCellString(formatter, row, 3);
                String deptName = getCellString(formatter, row, 4);

                if (name == null && username == null && email == null) {
                    continue;
                }

                Map<String, Object> rowResult = new LinkedHashMap<>();
                rowResult.put("row", i + 1);
                rowResult.put("username", username);
                try {
                    User u = new User();
                    u.setName(name);
                    u.setUsername(username);
                    u.setEmail(email);
                    u.setRole(role == null ? "EMPLOYEE" : role.trim().toUpperCase());
                    u.setPassword(generateRandomPassword());

                    if (deptName != null) {
                        Department found = null;
                        for (Department d : this.departmentService.getDepartments()) {
                            if (d.getName() != null && d.getName().trim().equalsIgnoreCase(deptName)) {
                                found = d;
                                break;
                            }
                        }
                        if (found == null) {
                            throw new IllegalArgumentException("Không tìm thấy phòng ban: " + deptName);
                        }
                        u.setDepartmentId(found);
                    }

                    this.createUser(u);
                    rowResult.put("status", "success");
                } catch (Exception e) {
                    rowResult.put("status", "error");
                    rowResult.put("message", e.getMessage());
                }
                results.add(rowResult);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Không đọc được file Excel: " + e.getMessage());
        }
        return results;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String getCellString(DataFormatter formatter, Row row, int idx) {
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(idx);
        if (cell == null) {
            return null;
        }
        String v = formatter.formatCellValue(cell).trim();
        return v.isEmpty() ? null : v;
    }

    @Override
    public User login(String username, String rawPassword) {
        User u = this.userRepo.getUserByUsername(username);
        if (u == null || !this.passwordEncoder.matches(rawPassword, u.getPassword())) {
            return null;
        }
        if (!u.getIsActive()) {
            throw new IllegalArgumentException("Tài khoản đã bị khoá. Vui lòng liên hệ quản trị viên để được hỗ trợ.");
        }
        return u;
    }

    @Override
    public void deactivateUser(long id) {
        this.userRepo.deactivateUser(id);
    }

    @Override
    public void reactivateUser(long id) {
        User u = this.userRepo.getUserById(id);
        if (u == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        u.setIsActive(true);
        this.userRepo.saveOrUpdate(u);
    }

    @Override
    public User updateUser(long id, User body) {
        User existing = this.userRepo.getUserById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        if ("ADMIN".equals(existing.getRole())) {
            throw new IllegalArgumentException("Không thể chỉnh sửa tài khoản ADMIN qua chức năng này");
        }
        if (body.getName() == null || body.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (body.getEmail() == null || body.getEmail().trim().isEmpty() || !body.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (body.getRole() == null || !List.of("EMPLOYEE", "TRAINER").contains(body.getRole())) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + body.getRole());
        }
        if (body.getDepartmentId() != null && body.getDepartmentId().getId() != null
                && this.departmentService.getDepartmentById(body.getDepartmentId().getId()) == null) {
            throw new IllegalArgumentException("Phòng ban không tồn tại");
        }

        String newEmail = body.getEmail().trim();
        User existedEmail = this.userRepo.getUserByEmail(newEmail);
        if (existedEmail != null && !existedEmail.getId().equals(id)) {
            throw new IllegalArgumentException("Email đã được sử dụng: " + newEmail);
        }

        existing.setName(body.getName().trim());
        existing.setEmail(newEmail);
        existing.setRole(body.getRole());
        existing.setDepartmentId(body.getDepartmentId());
        this.userRepo.saveOrUpdate(existing);
        return existing;
    }

    @Override
    public void changePassword(long userId, String oldPassword, String newPassword) {
        User u = this.userRepo.getUserById(userId);
        if (u == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        if (!this.userRepo.authenticate(u.getUsername(), oldPassword)) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        u.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepo.saveOrUpdate(u);
    }

}
