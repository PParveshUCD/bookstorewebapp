package com.example.bookstorewebapp;

import com.example.bookstorewebapp.model.Book;
import com.example.bookstorewebapp.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
@SpringBootApplication
public class BookStoreWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreWebappApplication.class, args);
    }

    @Bean
    public CommandLineRunner initBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() == 0) {
                bookRepository.save(new Book(null, "Clean Code", "Robert C. Martin", 2008, 35.99, 10, new ArrayList<>(), new ArrayList<>()));
                bookRepository.save(new Book(null, "Effective Java", "Joshua Bloch", 2018, 45.00, 5, new ArrayList<>(), new ArrayList<>()));
                bookRepository.save(new Book(null, "Spring in Action", "Craig Walls", 2022, 39.99, 7, new ArrayList<>(), new ArrayList<>()));
                bookRepository.save(new Book(null, "Java Concurrency in Practice", "Brian Goetz", 2006, 42.50, 4, new ArrayList<>(), new ArrayList<>()));
            }
        };
    }
}


