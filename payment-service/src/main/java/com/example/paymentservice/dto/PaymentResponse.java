package com.example.paymentservice.dto;

public record PaymentResponse(String orderId, String status, String message) {
}
