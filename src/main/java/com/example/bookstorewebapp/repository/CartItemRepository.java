package com.example.bookstorewebapp.repository;

import com.example.bookstorewebapp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
