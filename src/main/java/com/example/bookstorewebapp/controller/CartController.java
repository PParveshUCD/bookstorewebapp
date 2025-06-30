package com.example.bookstorewebapp.controller;

import com.example.bookstorewebapp.model.Cart;
import com.example.bookstorewebapp.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
//@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId, Principal principal) {
        cartService.addToCart(principal.getName(), bookId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        if (principal != null) {
            Cart cart = cartService.getCartForUser(principal.getName());
            if (cart == null) {
                model.addAttribute("cart", new Cart()); // empty cart to avoid null
            }
            model.addAttribute("cart", cart);
        }
        return "cart"; // you'll create cart.html
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long bookId, Principal principal) {
        cartService.removeFromCart(principal.getName(), bookId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/decrease")
    public String decreaseQuantity(@RequestParam Long bookId, Principal principal) {
        cartService.decreaseQuantity(principal.getName(), bookId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/increase")
    public String increaseQuantity(@RequestParam Long bookId, Principal principal) {
        cartService.increaseQuantity(principal.getName(), bookId);
        return "redirect:/cart";
    }

}
