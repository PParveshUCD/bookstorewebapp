package com.example.bookstorewebapp.controller;

import com.example.bookstorewebapp.model.Book;
import com.example.bookstorewebapp.repository.BookRepository;
import com.example.bookstorewebapp.model.User;
import com.example.bookstorewebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookRepository bookRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/add-book")
    public String showAddBookForm(Principal principal, Model model) {
        User user = userRepo.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/books";
        }
        return "add_book";
    }

    @PostMapping("/add-book")
    public String addBook(@RequestParam String title,
                          @RequestParam String author,
                          @RequestParam int year,
                          @RequestParam double price,
                          @RequestParam int copies,
                          Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/books";
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setPrice(price);
        book.setCopies(copies);

        bookRepo.save(book);
        return "redirect:/books";
    }

    @PostMapping("/delete-book")
    public String deleteOutOfStockBook(@RequestParam Long bookId, Principal principal, Model model) {
        User user = userRepo.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/books";
        }

        Book book = bookRepo.findById(bookId).orElse(null);
        if (book == null || book.getCopies() > 0) {
            return "redirect:/books";
        }

        // Check if book is referenced
        boolean usedInCart = book.getCartItems() != null && !book.getCartItems().isEmpty();
        boolean usedInOrder = book.getOrderItems() != null && !book.getOrderItems().isEmpty();

        if (usedInCart || usedInOrder) {
            model.addAttribute("error", "Cannot delete book. It is referenced in carts or orders.");
            model.addAttribute("books", bookRepo.findAll());
            return "admin_books";  // or your actual admin view name
        }

        bookRepo.delete(book);
        return "redirect:/books";
    }


}
