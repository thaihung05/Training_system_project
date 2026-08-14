/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.configs;

import com.tlh.filters.JwtFilter;
import com.tlh.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 *
 * @author LENOVO
 */
@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(
        basePackages = {
            "com.tlh.repository", 
            "com.tlh.service"
        })
public class SecurityConfigs {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/api/secure/users/**").hasAnyRole("ADMIN", "TRAINER")
                    .requestMatchers("/api/secure/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/secure/chains/**").authenticated()
                    .requestMatchers("/api/secure/chains/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/secure/regions/**").authenticated()
                    .requestMatchers("/api/secure/regions/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/secure/stores/**").authenticated()
                    .requestMatchers("/api/secure/stores/**").hasRole("ADMIN")
                    .requestMatchers("/api/secure/point-rules/**").hasRole("ADMIN")
                    .requestMatchers("/api/secure/badges/my").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/secure/badges").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/secure/badges/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/secure/badges/**").hasRole("ADMIN")
                    .requestMatchers("/api/secure/certificates/*/pdf-url").hasRole("ADMIN")
                    .requestMatchers("/api/secure/uploads/**").hasAnyRole("TRAINER", "ADMIN")
                    .requestMatchers("/api/secure/**").authenticated()
                    .anyRequest().permitAll()
            ).addFilterBefore(new JwtFilter(userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}