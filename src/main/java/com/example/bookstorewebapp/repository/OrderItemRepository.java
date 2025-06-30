package com.example.bookstorewebapp.repository;

import com.example.bookstorewebapp.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
