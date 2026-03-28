package com.example.orderservice.dto;

import java.math.BigDecimal;

public record OrderStatusResponse(String orderId, String status, BigDecimal amount) {
}
