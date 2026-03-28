package com.example.inventoryservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "inventory")
public class InventoryServiceProperties {

	@NotBlank
	private String failingOrderId = "302";

	public String getFailingOrderId() {
		return failingOrderId;
	}

	public void setFailingOrderId(String failingOrderId) {
		this.failingOrderId = failingOrderId;
	}
}
