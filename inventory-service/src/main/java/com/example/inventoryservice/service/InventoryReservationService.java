package com.example.inventoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.inventoryservice.config.InventoryServiceProperties;
import com.example.inventoryservice.dto.InventoryReserveRequest;
import com.example.inventoryservice.dto.InventoryResponse;

@Service
public class InventoryReservationService {

	private static final Logger log = LoggerFactory.getLogger(InventoryReservationService.class);

	private final InventoryServiceProperties properties;

	public InventoryReservationService(InventoryServiceProperties properties) {
		this.properties = properties;
	}

	public InventoryResponse reserve(InventoryReserveRequest request) {
		log.info("Received inventory reservation request for order {}", request.orderId());
		if (properties.getFailingOrderId().equals(request.orderId())) {
			log.warn("Deterministic inventory failure triggered for order {}", request.orderId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Inventory reservation failed for order %s".formatted(request.orderId()));
		}
		return new InventoryResponse(request.orderId(), "INVENTORY_RESERVED", "Inventory reserved successfully.");
	}
}
