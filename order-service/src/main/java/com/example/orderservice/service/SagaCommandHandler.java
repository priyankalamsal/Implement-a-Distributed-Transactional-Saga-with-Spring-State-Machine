package com.example.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;
import com.example.orderservice.repository.OrderRepository;

@Component
public class SagaCommandHandler {

	private static final Logger log = LoggerFactory.getLogger(SagaCommandHandler.class);

	private final OrderRepository orderRepository;
	private final PaymentClient paymentClient;
	private final InventoryClient inventoryClient;
	private final OrderStateMachineGateway stateMachineGateway;

	public SagaCommandHandler(
			OrderRepository orderRepository,
			PaymentClient paymentClient,
			InventoryClient inventoryClient,
			OrderStateMachineGateway stateMachineGateway) {
		this.orderRepository = orderRepository;
		this.paymentClient = paymentClient;
		this.inventoryClient = inventoryClient;
		this.stateMachineGateway = stateMachineGateway;
	}

	@Async("sagaTaskExecutor")
	@EventListener
	public void handle(ProcessPaymentCommand command) {
		orderRepository.findById(command.orderId()).ifPresentOrElse(order -> {
			if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
				log.debug("Skipping payment processing for order {} because it is already {}", order.getId(), order.getStatus());
				return;
			}
			try {
				paymentClient.processPayment(order);
				stateMachineGateway.sendEvent(order.getId(), SagaEvent.PAYMENT_SUCCESS);
			} catch (Exception exception) {
				log.warn("Payment processing failed for order {}: {}", order.getId(), exception.getMessage());
				stateMachineGateway.sendEvent(order.getId(), SagaEvent.PAYMENT_FAILED);
			}
		}, () -> log.warn("Payment command received for unknown order {}", command.orderId()));
	}

	@Async("sagaTaskExecutor")
	@EventListener
	public void handle(ReserveInventoryCommand command) {
		orderRepository.findById(command.orderId()).ifPresentOrElse(order -> {
			if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED) {
				log.debug("Skipping inventory reservation for order {} because it is already {}", order.getId(), order.getStatus());
				return;
			}
			try {
				inventoryClient.reserveInventory(order);
				stateMachineGateway.sendEvent(order.getId(), SagaEvent.INVENTORY_SUCCESS);
			} catch (Exception exception) {
				log.warn("Inventory reservation failed for order {}: {}", order.getId(), exception.getMessage());
				stateMachineGateway.sendEvent(order.getId(), SagaEvent.INVENTORY_FAILED);
			}
		}, () -> log.warn("Inventory command received for unknown order {}", command.orderId()));
	}

	@Async("sagaTaskExecutor")
	@EventListener
	public void handle(CancelPaymentCommand command) {
		try {
			paymentClient.cancelPayment(command.orderId());
		} catch (Exception exception) {
			log.error("Payment compensation failed for order {}", command.orderId(), exception);
		}
	}

	@Async("sagaTaskExecutor")
	@EventListener
	public void handle(CompleteOrderCommand command) {
		orderRepository.findById(command.orderId()).ifPresentOrElse(order -> {
			if (order.getStatus() != OrderStatus.INVENTORY_RESERVED) {
				log.debug("Skipping completion for order {} because it is already {}", order.getId(), order.getStatus());
				return;
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
				return;
			}
			stateMachineGateway.sendEvent(order.getId(), SagaEvent.COMPLETE_ORDER);
		}, () -> log.warn("Completion command received for unknown order {}", command.orderId()));
	}
}
