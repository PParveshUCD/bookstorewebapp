package com.example.bookstorewebapp.service;

import com.example.bookstorewebapp.model.*;
import com.example.bookstorewebapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private BookRepository bookRepo;
    @Autowired private CartItemRepository itemRepo;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepo;

    public void addToCart(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        Book book = bookRepo.findById(bookId).orElseThrow();
        // ✅ Check if the book is in stock
        if (book.getCopies() <= 0) {
            throw new IllegalStateException("Book out of stock");
        }

        CartItem item = null;
        for (CartItem ci : cart.getItems()) {
            if (ci.getBook().getId().equals(bookId)) {
                item = ci;
                break;
            }
        }

        if (item == null) {
            item = new CartItem();
            item.setBook(book);
            item.setCart(cart);
            item.setQuantity(1);
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }

        // ✅ Decrease book copies by 1
        book.setCopies(book.getCopies() - 1);
        cartRepo.save(cart);
        bookRepo.save(book);
    }

    public int getCartItemCount(String username) {
        Cart cart = getCartForUser(username);
        if (cart == null || cart.getItems() == null) return 0;

        return cart.getItems().stream().mapToInt(item -> item.getQuantity()).sum();
    }

    public Cart getCartForUser(String username) {
        User user = userRepository.findByUsername(username);
        return cartRepo.findByUser(user);
    }
    @Transactional
    public Order checkout(String username) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);
       // System.out.println("Username: " + username);
        //System.out.println("Cart found: " + (cart != null));
        log.debug("Cart fetched for user {}", username);
        if (cart != null) {
            System.out.println("Items in cart: " + cart.getItems().size());
        }

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }

        // Check stock
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book.getCopies() < item.getQuantity()) {
                throw new IllegalStateException("Not enough copies for book: " + book.getTitle());
            }
        }

        Order order = new Order();


        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        double total = 0;

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            book.setCopies(book.getCopies() - item.getQuantity());
            bookRepo.save(book);

            OrderItem oi = new OrderItem();
            oi.setBook(book);
            oi.setQuantity(item.getQuantity());
            oi.setPrice(book.getPrice()); // ✅ Track item price at time of order
            oi.setOrder(order);
            order.getItems().add(oi);

            total += book.getPrice() * item.getQuantity();
        }

        order.setTotal(total);
        orderRepo.save(order);

        itemRepo.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepo.save(cart);

        return order;
    }

    public void removeFromCart(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) return;

        CartItem itemToRemove = null;
        for (CartItem item : cart.getItems()) {
            if (item.getBook().getId().equals(bookId)) {
                Book book = item.getBook();
                book.setCopies(book.getCopies() + item.getQuantity());
                bookRepo.save(book);

                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            cart.getItems().remove(itemToRemove);
            itemRepo.delete(itemToRemove);
            cartRepo.save(cart);
        }
    }
    public void decreaseQuantity(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) return;

        CartItem itemToUpdate = null;
        for (CartItem item : cart.getItems()) {
            if (item.getBook().getId().equals(bookId)) {
                itemToUpdate = item;
                break;
            }
        }

        if (itemToUpdate != null) {
            if (itemToUpdate.getQuantity() > 1) {
                itemToUpdate.setQuantity(itemToUpdate.getQuantity() - 1);
                itemRepo.save(itemToUpdate);
            } else {
                cart.getItems().remove(itemToUpdate);
                itemRepo.delete(itemToUpdate);
            }
            cartRepo.save(cart);
        }
    }

    public void increaseQuantity(String username, Long bookId) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) return;

        for (CartItem item : cart.getItems()) {
            if (item.getBook().getId().equals(bookId)) {
                // Optional: Check stock before increasing
                Book book = item.getBook();
                if (item.getQuantity() < book.getCopies()) {
                    item.setQuantity(item.getQuantity() + 1);
                    itemRepo.save(item);
                }
                break;
            }
        }
    }


}
