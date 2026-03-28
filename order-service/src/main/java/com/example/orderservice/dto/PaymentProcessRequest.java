package com.example.orderservice.dto;

import java.math.BigDecimal;

public record PaymentProcessRequest(String orderId, Long customerId, BigDecimal amount) {
}
