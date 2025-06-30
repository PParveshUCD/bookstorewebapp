package com.example.bookstorewebapp.controller;

import com.example.bookstorewebapp.model.Order;
import com.example.bookstorewebapp.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.bookstorewebapp.model.*;
import com.example.bookstorewebapp.repository.*;

import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;


@Controller
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepo;
    @Autowired private BookRepository bookRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private CartItemRepository itemRepo;

    // ✅ This handles the POST /checkout from a form
     @PostMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        try {
            Order order = cartService.checkout(principal.getName());

            model.addAttribute("order", order);
            System.out.println("Checkout successful, returning order_success");
            return "order_success";
        } catch (Exception e) {
            model.addAttribute("error", "Checkout failed: " + e.getMessage());
            Cart cart = cartService.getCartForUser(principal.getName());
            model.addAttribute("cart", cart);
            return "cart";
        }
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        Cart cart = cartService.getCartForUser(principal.getName());
        if (cart == null || cart.getItems().isEmpty()) {
            model.addAttribute("error", "Cart is empty.");
            return "cart";
        }
        model.addAttribute("total", cart.getItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum());
        return "checkout";
    }
}
