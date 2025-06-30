package com.example.bookstorewebapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ADMIN or CUSTOMER

    // Customer-specific fields
    private String name;
    private String surname;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String email;
}
