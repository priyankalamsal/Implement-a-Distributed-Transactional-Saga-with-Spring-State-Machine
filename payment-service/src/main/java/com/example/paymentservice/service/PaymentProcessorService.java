package com.example.paymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.paymentservice.config.PaymentServiceProperties;
import com.example.paymentservice.dto.PaymentProcessRequest;
import com.example.paymentservice.dto.PaymentResponse;

@Service
public class PaymentProcessorService {

	private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);

	private final PaymentServiceProperties properties;

	public PaymentProcessorService(PaymentServiceProperties properties) {
		this.properties = properties;
	}

	public PaymentResponse process(PaymentProcessRequest request) {
		log.info("Received payment process request for order {}", request.orderId());
		delayIfConfigured();
		if (properties.getFailingOrderId().equals(request.orderId())) {
			log.warn("Deterministic payment failure triggered for order {}", request.orderId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Payment processing failed for order %s".formatted(request.orderId()));
		}
		return new PaymentResponse(request.orderId(), "PAYMENT_COMPLETED", "Payment processed successfully.");
	}

	public PaymentResponse cancel(String orderId) {
		log.info("Received payment cancel request for order {}", orderId);
		return new PaymentResponse(orderId, "PAYMENT_CANCELLED", "Payment cancelled successfully.");
	}

	private void delayIfConfigured() {
		if (properties.getProcessingDelayMs() <= 0) {
			return;
		}
		try {
			Thread.sleep(properties.getProcessingDelayMs());
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing was interrupted");
		}
	}
}
