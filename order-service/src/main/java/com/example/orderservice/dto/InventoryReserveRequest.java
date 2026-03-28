package com.example.orderservice.dto;

public record InventoryReserveRequest(String orderId, Long productId, Integer quantity) {
}
