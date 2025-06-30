package com.example.bookstorewebapp.repository;

import com.example.bookstorewebapp.model.Cart;
import com.example.bookstorewebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}
