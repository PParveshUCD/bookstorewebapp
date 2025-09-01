package com.example.bookstorewebapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    // Tiny blocklist for very common passwords (extend as you like)
    private static final Set<String> BLOCKLIST = Set.of(
            "password","Password123","123456","qwerty","letmein","admin","welcome"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return false;
        if (BLOCKLIST.contains(value)) return false;
        if (value.length() < 12) return false;

        boolean upper=false, lower=false, digit=false, symbol=false;
        for (char c : value.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else symbol = true;
        }
        int classes = (upper?1:0) + (lower?1:0) + (digit?1:0) + (symbol?1:0);
        return classes >= 4; // require all four
    }
}
