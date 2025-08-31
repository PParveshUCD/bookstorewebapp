// src/main/java/com/example/bookstorewebapp/config/PiiBackfill.java
package com.example.bookstorewebapp.config;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PiiBackfill implements CommandLineRunner {
    private final UserRepository repo;
    @Override public void run(String... args) {
        repo.findAll().forEach(u -> repo.save(u)); // JPA converter encrypts on save
    }
}
