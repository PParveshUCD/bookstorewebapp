package com.example.bookstorewebapp.model;

import com.example.bookstorewebapp.validation.StrongPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.example.bookstorewebapp.config.CryptoAttributeConverter;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    private String username;        // kept in clear


    // replace your current @Size on password
    @StrongPassword
    private String password;
    //@NotBlank @Size(min = 8, max = 100)
    //private String password;        // BCrypt hash

    @NotBlank @Pattern(regexp = "ADMIN|CUSTOMER")
    private String role;            // kept in clear

    // ---- PII fields (you can re-enable @Convert once ready) ----
    // @Convert(converter = CryptoAttributeConverter.class)
    @Size(max = 100)
    private String name;

    // @Convert(converter = CryptoAttributeConverter.class)
    @Size(max = 100)
    private String surname;

    // @Convert(converter = CryptoAttributeConverter.class)
    @Size(max = 10)
    private String dateOfBirth;

    // @Convert(converter = CryptoAttributeConverter.class)
    @Size(max = 255)
    private String address;

    // @Convert(converter = CryptoAttributeConverter.class)
    @Size(max = 32) @Pattern(regexp = "^[0-9+()\\-\\s]*$")
    private String phone;

    // @Convert(converter = CryptoAttributeConverter.class)
    @Email @Size(max = 190)
    private String email;

    // ---- A04 protections ----

    /** Brute-force throttling (CWE-307) */
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    /** Until when the account is locked (null = not locked) */
    private Instant lockUntil;

    /** MFA flag (CWE-654) */
    @Column(nullable = false)
    private boolean mfaEnabled = false;

    /** Encrypted TOTP secret (enable @Convert if APP_ENC_KEY is set) */
    @Convert(converter = CryptoAttributeConverter.class)
    @Column(length = 256)
    private String mfaSecret;
}
