/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.filters;

import com.tlh.pojo.User;
import com.tlh.repository.UserRepository;
import com.tlh.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author LENOVO
 */
public class JwtFilter implements Filter {

    private final UserRepository userRepository;

    public JwtFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().startsWith(String.format("%s/api/secure", httpRequest.getContextPath()))) {

            String header = httpRequest.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                unauthorized((HttpServletResponse) response, "Thiếu hoặc sai định dạng token xác thực.");
                return;
            } else {
                String token = header.substring(7);
                try {
                    String username = JwtUtils.validateTokenAndGetUsername(token);
                    if (username != null) {
                        User user = this.userRepository.getUserByUsername(username);
                        if (user == null || !user.getIsActive()) {
                            unauthorized((HttpServletResponse) response, "Tài khoản không còn hoạt động hoặc không tồn tại.");
                            return;
                        }

                        httpRequest.setAttribute("username", username);

                        String role = user.getRole();
                        List<SimpleGrantedAuthority> authorities = role != null
                                ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                : List.of();

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        chain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            unauthorized((HttpServletResponse) response, "Token không hợp lệ hoặc đã hết hạn.");
            return;
        }

        chain.doFilter(request, response);
    }
}
