package com.example.orderservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

	List<OrderEntity> findByStatusIn(Collection<OrderStatus> statuses);
}
