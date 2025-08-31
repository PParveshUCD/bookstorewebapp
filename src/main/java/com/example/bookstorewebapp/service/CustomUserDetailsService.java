package com.example.bookstorewebapp.service;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.bookstorewebapp.security.LoginThrottleService;

import java.util.Collections;
import java.util.List;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired private LoginThrottleService throttle;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        throttle.ensureNotLocked(user); // ⬅️ block locked accounts
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );



    }
}
