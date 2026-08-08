/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.User;
import com.tlh.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Override
    public void sendAccountCreatedEmail(User u, String rawPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(env.getProperty("mail.smtp.from"));
            message.setTo(u.getEmail());
            message.setSubject("Tài khoản Hệ thống Đào tạo của bạn đã được tạo");
            message.setText(
                    "Xin chào " + u.getName() + ",\n\n"
                    + "Tài khoản của bạn trên Hệ thống Đào tạo đã được tạo:\n"
                    + "Tên đăng nhập: " + u.getUsername() + "\n"
                    + "Mật khẩu: " + rawPassword + "\n\n"
                    + "Vui lòng đăng nhập và đổi mật khẩu sau lần đăng nhập đầu tiên."
            );
            this.mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Gửi email tạo tài khoản thất bại: " + e.getMessage());
        }
    }
}
