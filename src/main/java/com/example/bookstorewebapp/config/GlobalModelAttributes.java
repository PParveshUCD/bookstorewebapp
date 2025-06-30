package com.example.bookstorewebapp.config;

import com.example.bookstorewebapp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addCartItemCount(Model model, Principal principal) {
        if (principal != null) {
            int count = cartService.getCartItemCount(principal.getName());
            model.addAttribute("cartItemCount", count);
        }
    }
}

