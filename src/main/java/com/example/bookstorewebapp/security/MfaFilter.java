package com.example.bookstorewebapp.security;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MfaFilter extends OncePerRequestFilter {

    private final UserRepository users;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        if (path.startsWith("/admin")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                User u = users.findByUsername(auth.getName());
                boolean needMfa = (u != null && "ADMIN".equals(u.getRole()) && u.isMfaEnabled());
                Boolean ok = (Boolean) req.getSession().getAttribute("MFA_OK");
                if (needMfa && (ok == null || !ok)) {
                    res.sendRedirect(req.getContextPath() + "/mfa/challenge");
                    return;
                }
            }
        }
        chain.doFilter(req, res);
    }
}
