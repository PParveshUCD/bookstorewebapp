package com.example.bookstorewebapp.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
public class SecurityAuditListener {

    private static final Logger AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent e) {
        AUDIT.info("auth_success user={}", safe(e.getAuthentication().getName()));
    }

    @EventListener
    public void onBadCreds(AuthenticationFailureBadCredentialsEvent e) {
        String principal = String.valueOf(e.getAuthentication().getPrincipal());
        AUDIT.warn("auth_failure_bad_credentials user={}", safe(principal));
    }

    @EventListener
    public void onDisabled(AuthenticationFailureDisabledEvent e) {
        AUDIT.warn("auth_failure_disabled user={}", safe(e.getAuthentication().getName()));
    }

    @EventListener
    public void onLocked(AuthenticationFailureLockedEvent e) {
        AUDIT.warn("auth_failure_locked user={}", safe(e.getAuthentication().getName()));
    }

    @EventListener
    public void onDenied(AuthorizationDeniedEvent<?> e) {
        Supplier<Authentication> supplier = e.getAuthentication(); // Supplier in Security 6+
        Authentication auth = (supplier != null) ? supplier.get() : null;

        String user = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anon";
        AuthorizationDecision decision = e.getAuthorizationDecision();

        AUDIT.warn("authz_denied user={} decision={} details={}",
                safe(user),
                (decision != null ? decision.isGranted() : null),
                safe(String.valueOf(e.getSource())));
    }

    private String safe(String in) {
        if (in == null) return "";
        return in.replace("\r", "_").replace("\n", "_");
    }
}
