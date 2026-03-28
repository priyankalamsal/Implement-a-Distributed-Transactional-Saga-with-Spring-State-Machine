package com.example.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderStatusResponse;
import com.example.orderservice.service.OrderSagaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderSagaService orderSagaService;

	public OrderController(OrderSagaService orderSagaService) {
		this.orderSagaService = orderSagaService;
	}

	@PostMapping
	public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderSagaService.createOrder(request));
	}

	@GetMapping("/{orderId}")
	public OrderStatusResponse getOrder(@PathVariable String orderId) {
		return orderSagaService.getOrder(orderId);
	}
}
