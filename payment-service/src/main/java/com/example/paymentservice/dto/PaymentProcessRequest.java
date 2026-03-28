package com.example.paymentservice.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentProcessRequest(
		@NotBlank String orderId,
		@NotNull Long customerId,
		@NotNull @DecimalMin("0.01") BigDecimal amount) {
}
