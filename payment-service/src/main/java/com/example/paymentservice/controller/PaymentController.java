package com.example.paymentservice.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.paymentservice.dto.PaymentProcessRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.service.PaymentProcessorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentProcessorService paymentProcessorService;

	public PaymentController(PaymentProcessorService paymentProcessorService) {
		this.paymentProcessorService = paymentProcessorService;
	}

	@PostMapping("/process")
	public PaymentResponse process(@Valid @RequestBody PaymentProcessRequest request) {
		return paymentProcessorService.process(request);
	}

	@PostMapping("/{orderId}/cancel")
	public PaymentResponse cancel(@PathVariable String orderId) {
		return paymentProcessorService.cancel(orderId);
	}
}
