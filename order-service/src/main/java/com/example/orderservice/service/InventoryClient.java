package com.example.orderservice.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.dto.InventoryReserveRequest;

@Component
public class InventoryClient {

	private final RestClient restClient;

	public InventoryClient(@Qualifier("inventoryRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public void reserveInventory(OrderEntity order) {
		restClient.post()
				.uri("/api/inventory/reserve")
				.contentType(APPLICATION_JSON)
				.body(new InventoryReserveRequest(order.getId(), order.getProductId(), order.getQuantity()))
				.retrieve()
				.toBodilessEntity();
	}
}
