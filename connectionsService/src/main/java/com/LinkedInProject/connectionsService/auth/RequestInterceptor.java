package com.LinkedInProject.connectionsService.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        String userId = request.getHeader("X-User-Id");

        if (userId != null && !userId.isBlank()) {
            try {
                AuthContextHolder.setCurrentUserId(Long.valueOf(userId.trim()));
            } catch (NumberFormatException e) {
                // Log or handle invalid numeric user IDs gracefully
                AuthContextHolder.clear();
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) throws Exception {

        AuthContextHolder.clear();
    }
}