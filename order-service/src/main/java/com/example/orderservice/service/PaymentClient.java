package com.example.orderservice.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.dto.PaymentProcessRequest;

@Component
public class PaymentClient {

	private final RestClient restClient;

	public PaymentClient(@Qualifier("paymentRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public void processPayment(OrderEntity order) {
		restClient.post()
				.uri("/api/payments/process")
				.contentType(APPLICATION_JSON)
				.body(new PaymentProcessRequest(order.getId(), order.getCustomerId(), order.getAmount()))
				.retrieve()
				.toBodilessEntity();
	}

	public void cancelPayment(String orderId) {
		restClient.post()
				.uri("/api/payments/{orderId}/cancel", orderId)
				.retrieve()
				.toBodilessEntity();
	}
}
