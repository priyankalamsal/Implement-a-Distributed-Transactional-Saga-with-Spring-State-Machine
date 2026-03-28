package com.example.orderservice.service;

import com.example.orderservice.domain.SagaEvent;

public interface OrderStateMachineGateway {

	boolean sendEvent(String orderId, SagaEvent event);
}
