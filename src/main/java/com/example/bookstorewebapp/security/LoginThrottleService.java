package com.example.bookstorewebapp.security;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoginThrottleService {
    private final UserRepository users;

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10);

    public void onSuccess(String username) {
        User u = users.findByUsername(username);
        if (u == null) return;
        u.setFailedLoginAttempts(0);
        u.setLockUntil(null);
        users.save(u);
    }

    public void onFailure(String username) {
        User u = users.findByUsername(username);
        if (u == null) return; // don’t leak enumeration
        int attempts = (u.getFailedLoginAttempts() == 0 ? 0 : u.getFailedLoginAttempts()) + 1;
        u.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_ATTEMPTS) {
            u.setLockUntil(Instant.now().plus(LOCK_DURATION));
        }
        users.save(u);
    }

    /** Throw on locked accounts */
    public void ensureNotLocked(User u) {
        if (u.getLockUntil() != null && Instant.now().isBefore(u.getLockUntil())) {
            throw new org.springframework.security.authentication.LockedException("Account temporarily locked");
        }
    }
}
