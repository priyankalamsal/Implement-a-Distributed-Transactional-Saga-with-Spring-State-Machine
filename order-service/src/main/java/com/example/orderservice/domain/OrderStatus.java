package com.example.orderservice.domain;

public enum OrderStatus {
	ORDER_CREATED,
	PAYMENT_PENDING,
	PAYMENT_COMPLETED,
	INVENTORY_RESERVED,
	ORDER_COMPLETED,
	ORDER_FAILED;

	public boolean isTerminal() {
		return this == ORDER_COMPLETED || this == ORDER_FAILED;
	}
}
