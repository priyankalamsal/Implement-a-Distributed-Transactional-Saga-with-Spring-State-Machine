package com.example.orderservice.service;

import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;
import com.example.orderservice.repository.OrderRepository;

@Component
public class SagaRecoveryService {

	private static final Logger log = LoggerFactory.getLogger(SagaRecoveryService.class);

	private final OrderRepository orderRepository;
	private final OrderStateMachineGateway stateMachineGateway;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final Executor sagaTaskExecutor;

	public SagaRecoveryService(
			OrderRepository orderRepository,
			OrderStateMachineGateway stateMachineGateway,
			ApplicationEventPublisher applicationEventPublisher,
			Executor sagaTaskExecutor) {
		this.orderRepository = orderRepository;
		this.stateMachineGateway = stateMachineGateway;
		this.applicationEventPublisher = applicationEventPublisher;
		this.sagaTaskExecutor = sagaTaskExecutor;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void recoverInFlightSagas() {
		List<OrderEntity> inFlightOrders = orderRepository.findByStatusIn(List.of(
				OrderStatus.ORDER_CREATED,
				OrderStatus.PAYMENT_PENDING,
				OrderStatus.PAYMENT_COMPLETED,
				OrderStatus.INVENTORY_RESERVED));

		for (OrderEntity order : inFlightOrders) {
			sagaTaskExecutor.execute(() -> recover(order));
		}
	}

	private void recover(OrderEntity order) {
		log.info("Recovering order {} from state {}", order.getId(), order.getStatus());
		switch (order.getStatus()) {
			case ORDER_CREATED -> stateMachineGateway.sendEvent(order.getId(), SagaEvent.CREATE_ORDER);
			case PAYMENT_PENDING -> applicationEventPublisher.publishEvent(new ProcessPaymentCommand(order.getId()));
			case PAYMENT_COMPLETED -> applicationEventPublisher.publishEvent(new ReserveInventoryCommand(order.getId()));
			case INVENTORY_RESERVED -> applicationEventPublisher.publishEvent(new CompleteOrderCommand(order.getId()));
			default -> {
			}
		}
	}
}
