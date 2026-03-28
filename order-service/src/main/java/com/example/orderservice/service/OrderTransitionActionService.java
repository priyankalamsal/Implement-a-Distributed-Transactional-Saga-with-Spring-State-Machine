package com.example.orderservice.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;
import com.example.orderservice.repository.OrderRepository;

@Service
public class OrderTransitionActionService {

	private final OrderRepository orderRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	public OrderTransitionActionService(OrderRepository orderRepository, ApplicationEventPublisher applicationEventPublisher) {
		this.orderRepository = orderRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Transactional
	public void onPaymentPending(StateContext<OrderStatus, SagaEvent> context) {
		String orderId = orderId(context);
		updateStatus(orderId, OrderStatus.PAYMENT_PENDING);
		applicationEventPublisher.publishEvent(new ProcessPaymentCommand(orderId));
	}

	@Transactional
	public void onPaymentCompleted(StateContext<OrderStatus, SagaEvent> context) {
		String orderId = orderId(context);
		updateStatus(orderId, OrderStatus.PAYMENT_COMPLETED);
		applicationEventPublisher.publishEvent(new ReserveInventoryCommand(orderId));
	}

	@Transactional
	public void onPaymentFailed(StateContext<OrderStatus, SagaEvent> context) {
		updateStatus(orderId(context), OrderStatus.ORDER_FAILED);
	}

	@Transactional
	public void onInventoryReserved(StateContext<OrderStatus, SagaEvent> context) {
		String orderId = orderId(context);
		updateStatus(orderId, OrderStatus.INVENTORY_RESERVED);
		applicationEventPublisher.publishEvent(new CompleteOrderCommand(orderId));
	}

	@Transactional
	public void onInventoryFailed(StateContext<OrderStatus, SagaEvent> context) {
		String orderId = orderId(context);
		updateStatus(orderId, OrderStatus.ORDER_FAILED);
		applicationEventPublisher.publishEvent(new CancelPaymentCommand(orderId));
	}

	@Transactional
	public void onOrderCompleted(StateContext<OrderStatus, SagaEvent> context) {
		updateStatus(orderId(context), OrderStatus.ORDER_COMPLETED);
	}

	private void updateStatus(String orderId, OrderStatus status) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalStateException("Order %s was not found during saga transition".formatted(orderId)));
		order.setStatus(status);
		orderRepository.save(order);
	}

	private String orderId(StateContext<OrderStatus, SagaEvent> context) {
		return context.getStateMachine().getId();
	}
}
