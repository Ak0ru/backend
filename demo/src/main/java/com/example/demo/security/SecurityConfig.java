package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // 1) Полностью игнорируем /worldtime/**
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/worldtime/**");
    }

    // 2) Основная цепочка безопасности
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 2.1) Stateless: не сохраняем JSESSIONID, всё по JWT
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 2.2) CSRF для REST-API отключаем
                .csrf(csrf -> csrf.disable())
                // 2.3) Ставим наш JWT-фильтр **до** UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                // 2.4) Правила доступа
                .authorizeHttpRequests(auth -> auth

                        // публичные
                        .requestMatchers("/api/auth/**", "/api/mail/**", "/actuator/**")
                        .permitAll()

                        // ADMIN → /api/users/**
                        .requestMatchers(HttpMethod.GET, "/api/users/**")
                        .hasRole("ADMIN")

                        // ADMIN/TEACHER/STUDENT → /api/students и /api/students/filter
                        .requestMatchers(HttpMethod.GET, "/api/students", "/api/students/**", "/api/students/filter")
                        .hasAnyRole("ADMIN","TEACHER","STUDENT")

                        // любой аутентифицированный → отправка писем с вложениями
                        .requestMatchers(HttpMethod.POST, "/api/send-email-with-attachment")
                        .authenticated()

                        // любой аутентифицированный → все таски
                        .requestMatchers("/api/tasks/**")
                        .authenticated()

                        // всё остальное требует аутентификации
                        .anyRequest().authenticated()
                )
                // 2.5) Где грузить UserDetails (для роли и пароля)
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    // 3) Поверяем пароли
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 4) Для работы AuthenticationManager внутри фильтра логина
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
