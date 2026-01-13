package com.tp.frontend.config;

import com.tp.frontend.web.SessionKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("rid", requestId);

        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        MDC.put("ip", ip);

        HttpSession session = request.getSession(false);
        if (session != null) {
            MDC.put("sid", session.getId());

            Object u = session.getAttribute(SessionKeys.USERNAME);
            if (u != null) MDC.put("user", u.toString());

            Object r = session.getAttribute(SessionKeys.ROLE);
            if (r != null) MDC.put("role", r.toString());
        } else {
            MDC.put("sid", "-");
            MDC.put("user", "anon");
            MDC.put("role", "-");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.put("status", String.valueOf(response.getStatus()));
            MDC.clear(); // MUY importante para no “contaminar” threads
        }
    }
}
