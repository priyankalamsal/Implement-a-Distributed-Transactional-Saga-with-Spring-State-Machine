package com.example.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "payment")
public class PaymentServiceProperties {

	@NotBlank
	private String failingOrderId = "201";

	@Min(0)
	private long processingDelayMs = 0L;

	public String getFailingOrderId() {
		return failingOrderId;
	}

	public void setFailingOrderId(String failingOrderId) {
		this.failingOrderId = failingOrderId;
	}

	public long getProcessingDelayMs() {
		return processingDelayMs;
	}

	public void setProcessingDelayMs(long processingDelayMs) {
		this.processingDelayMs = processingDelayMs;
	}
}
