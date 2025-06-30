package com.example.bookstorewebapp.repository;

import com.example.bookstorewebapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
