package com.example.bookstorewebapp.controller;

import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

//    / ✅ LOGIN VIEW (required because formLogin().loginPage("/login") expects a view named "login")
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // resolves to src/main/resources/templates/login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult binding,
                               Model model,
                               RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CUSTOMER");
        userRepository.save(user);
        ra.addFlashAttribute("success", "Account created");
        return "redirect:/login";
    }
}
