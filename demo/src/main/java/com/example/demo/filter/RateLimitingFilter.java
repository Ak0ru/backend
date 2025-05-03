package com.example.demo.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    // Параметры rate limit
    private static final long WINDOW_MS = 60_000;  // окно: 1 минута
    private static final int MAX_REQUESTS = 5;     // макс. запросов в окне

    // Хранилище: IP → счётчик
    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        // Обновляем или создаём счётчик для этого IP
        RequestCounter counter = counters.compute(ip, (key, old) -> {
            if (old == null || now - old.getWindowStart() >= WINDOW_MS) {
                // Новое окно
                return new RequestCounter(now, 1);
            } else {
                // Текущее окно — инкримент
                old.setCount(old.getCount() + 1);
                return old;
            }
        });

        if (counter.getCount() > MAX_REQUESTS) {
            // Превышен лимит — 429 и выход
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }

        // Всё в пределах лимита — пропускаем дальше
        filterChain.doFilter(request, response);
    }
}
