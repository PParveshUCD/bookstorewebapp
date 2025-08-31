package com.example.bookstorewebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.example.bookstorewebapp.config.CryptoAttributeConverter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String username;        // keep in clear
    @NotBlank @Size(min = 8, max = 100)
    private String password;        // stored as BCrypt hash elsewhere
    @NotBlank @Pattern(regexp = "ADMIN|CUSTOMER")
    private String role;            // keep in clear

    // Encrypt PII fields
    //@Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 512)
    @Size(max = 100)
    private String name;

    //@Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 512)
    @Size(max = 100)
    private String surname;

    //@Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 512)
    @Size(max = 10)
    private String dateOfBirth;

    //@Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 1024)
    @Size(max = 255)
    private String address;

    //@Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 512)
    @Size(max = 32) @Pattern(regexp = "^[0-9+()\\-\\s]*$")
    private String phone;

   // @Convert(converter = CryptoAttributeConverter.class)
    //@Column(length = 512)
   @Email
   @Size(max = 190) // 190 fits common unique index limits
    private String email;
}
