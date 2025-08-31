package com.example.bookstorewebapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class AuthEvents {
    private final LoginThrottleService throttle;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent e) {
        String username = e.getAuthentication().getName();
        throttle.onSuccess(username);
    }

    @EventListener
    public void onBadCreds(AuthenticationFailureBadCredentialsEvent e) {
        String username = String.valueOf(e.getAuthentication().getPrincipal());
        throttle.onFailure(username);
    }
}
