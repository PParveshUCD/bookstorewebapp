package com.example.bookstorewebapp.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.bookstorewebapp.config.CryptoAttributeConverter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;        // keep in clear
    private String password;        // stored as BCrypt hash elsewhere
    private String role;            // keep in clear

    // Encrypt PII fields
    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 512)
    private String name;

    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 512)
    private String surname;

    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 512)
    private String dateOfBirth;

    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 1024)
    private String address;

    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 512)
    private String phone;

    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 512)
    private String email;
}
