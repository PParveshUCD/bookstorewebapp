package com.example.bookstorewebapp.controller;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import com.example.bookstorewebapp.security.TotpService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class MfaController {

    private final UserRepository users;
    private final TotpService totp = new TotpService();
    private final SecureRandom rng = new SecureRandom();

    @GetMapping("/mfa/setup")
    public String setup(@AuthenticationPrincipal UserDetails principal, Model model) {
        User u = users.findByUsername(principal.getUsername());
        if (u.isMfaEnabled() && u.getMfaSecret() != null) return "redirect:/mfa/challenge";
        byte[] secretBytes = new byte[20]; rng.nextBytes(secretBytes);
        String secretB64 = Base64.getEncoder().encodeToString(secretBytes);
        u.setMfaSecret(secretB64);
        users.save(u);

        // Show secret/QR (generate otpauth URI if you have Base32 handy)
        model.addAttribute("secret", secretB64);
        model.addAttribute("account", u.getUsername());
        return "mfa_setup";
    }

    @PostMapping("/mfa/enable")
    public String enable(@AuthenticationPrincipal UserDetails principal,
                         @RequestParam String code) {
        User u = users.findByUsername(principal.getUsername());
        if (u.getMfaSecret() == null) return "redirect:/mfa/setup";
        if (totp.verify(u.getMfaSecret(), code)) {
            u.setMfaEnabled(true);
            users.save(u);
            return "redirect:/"; // enabled
        }
        return "redirect:/mfa/setup?error";
    }

    @GetMapping("/mfa/challenge")
    public String challenge() { return "mfa_challenge"; }

    @PostMapping("/mfa/challenge")
    public String verify(@AuthenticationPrincipal UserDetails principal,
                         @RequestParam String code,
                         HttpSession session) {
        User u = users.findByUsername(principal.getUsername());
        if (u != null && u.isMfaEnabled() && totp.verify(u.getMfaSecret(), code)) {
            session.setAttribute("MFA_OK", Boolean.TRUE);
            return "redirect:/";
        }
        return "redirect:/mfa/challenge?error";
    }
}
