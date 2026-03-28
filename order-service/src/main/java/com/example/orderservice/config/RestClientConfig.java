package com.example.orderservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	@Qualifier("paymentRestClient")
	public RestClient paymentRestClient(RestClient.Builder builder, DownstreamServiceProperties properties) {
		return builder.baseUrl(properties.getPaymentServiceUrl())
				.requestFactory(clientHttpRequestFactory(properties))
				.build();
	}

	@Bean
	@Qualifier("inventoryRestClient")
	public RestClient inventoryRestClient(RestClient.Builder builder, DownstreamServiceProperties properties) {
		return builder.baseUrl(properties.getInventoryServiceUrl())
				.requestFactory(clientHttpRequestFactory(properties))
				.build();
	}

	private ClientHttpRequestFactory clientHttpRequestFactory(DownstreamServiceProperties properties) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(properties.getConnectTimeoutMs());
		factory.setReadTimeout(properties.getReadTimeoutMs());
		return factory;
	}
}
