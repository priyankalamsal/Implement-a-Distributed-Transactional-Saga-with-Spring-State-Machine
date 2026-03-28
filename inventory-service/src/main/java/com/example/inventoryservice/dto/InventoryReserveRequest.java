package com.example.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryReserveRequest(
		@NotBlank String orderId,
		@NotNull Long productId,
		@NotNull @Positive Integer quantity) {
}
