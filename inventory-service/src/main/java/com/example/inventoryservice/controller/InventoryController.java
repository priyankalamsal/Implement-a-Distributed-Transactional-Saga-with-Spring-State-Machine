package com.example.inventoryservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventoryservice.dto.InventoryReserveRequest;
import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.service.InventoryReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryReservationService inventoryReservationService;

	public InventoryController(InventoryReservationService inventoryReservationService) {
		this.inventoryReservationService = inventoryReservationService;
	}

	@PostMapping("/reserve")
	public InventoryResponse reserve(@Valid @RequestBody InventoryReserveRequest request) {
		return inventoryReservationService.reserve(request);
	}
}
