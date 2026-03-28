package com.example.orderservice.domain;

public enum SagaEvent {
	CREATE_ORDER,
	PAYMENT_SUCCESS,
	PAYMENT_FAILED,
	INVENTORY_SUCCESS,
	INVENTORY_FAILED,
	COMPLETE_ORDER
}
