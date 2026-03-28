package com.example.orderservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateOrderRequest(
		String orderId,
		@NotNull Long customerId,
		@NotNull Long productId,
		@NotNull @Positive Integer quantity,
		@NotNull @DecimalMin("0.01") BigDecimal unitPrice) {
}
