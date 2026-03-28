package com.example.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "saga.clients")
public class DownstreamServiceProperties {

	@NotBlank
	private String paymentServiceUrl = "http://localhost:8081";

	@NotBlank
	private String inventoryServiceUrl = "http://localhost:8082";

	@Min(1)
	private int connectTimeoutMs = 2000;

	@Min(1)
	private int readTimeoutMs = 5000;

	public String getPaymentServiceUrl() {
		return paymentServiceUrl;
	}

	public void setPaymentServiceUrl(String paymentServiceUrl) {
		this.paymentServiceUrl = paymentServiceUrl;
	}

	public String getInventoryServiceUrl() {
		return inventoryServiceUrl;
	}

	public void setInventoryServiceUrl(String inventoryServiceUrl) {
		this.inventoryServiceUrl = inventoryServiceUrl;
	}

	public int getConnectTimeoutMs() {
		return connectTimeoutMs;
	}

	public void setConnectTimeoutMs(int connectTimeoutMs) {
		this.connectTimeoutMs = connectTimeoutMs;
	}

	public int getReadTimeoutMs() {
		return readTimeoutMs;
	}

	public void setReadTimeoutMs(int readTimeoutMs) {
		this.readTimeoutMs = readTimeoutMs;
	}
}
