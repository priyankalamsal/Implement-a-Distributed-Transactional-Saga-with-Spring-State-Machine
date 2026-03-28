package com.example.orderservice.service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderStatusResponse;
import com.example.orderservice.repository.OrderRepository;

@Service
public class OrderSagaService implements OrderStateMachineGateway {

	private static final Logger log = LoggerFactory.getLogger(OrderSagaService.class);

	private final OrderRepository orderRepository;
	private final StateMachineService<OrderStatus, SagaEvent> stateMachineService;
	private final OrderStateMachineListenerFactory listenerFactory;
	private final Executor sagaTaskExecutor;

	public OrderSagaService(
			OrderRepository orderRepository,
			StateMachineService<OrderStatus, SagaEvent> stateMachineService,
			OrderStateMachineListenerFactory listenerFactory,
			Executor sagaTaskExecutor) {
		this.orderRepository = orderRepository;
		this.stateMachineService = stateMachineService;
		this.listenerFactory = listenerFactory;
		this.sagaTaskExecutor = sagaTaskExecutor;
	}

	@Transactional
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		String orderId = StringUtils.hasText(request.orderId()) ? request.orderId() : UUID.randomUUID().toString();
		if (orderRepository.existsById(orderId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Order %s already exists".formatted(orderId));
		}

		BigDecimal amount = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
		OrderEntity order = new OrderEntity();
		order.setId(orderId);
		order.setCustomerId(request.customerId());
		order.setProductId(request.productId());
		order.setQuantity(request.quantity());
		order.setUnitPrice(request.unitPrice());
		order.setAmount(amount);
		order.setStatus(OrderStatus.ORDER_CREATED);
		orderRepository.save(order);

		sagaTaskExecutor.execute(() -> sendEvent(orderId, SagaEvent.CREATE_ORDER));

		return new CreateOrderResponse(orderId, OrderStatus.ORDER_CREATED.name(), "Order creation process initiated.");
	}

	@Transactional(readOnly = true)
	public OrderStatusResponse getOrder(String orderId) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order %s was not found".formatted(orderId)));
		return new OrderStatusResponse(order.getId(), order.getStatus().name(), order.getAmount());
	}

	@Override
	public boolean sendEvent(String orderId, SagaEvent event) {
		return withStateMachine(orderId, stateMachine -> {
			Message<SagaEvent> message = MessageBuilder.withPayload(event)
					.setHeader("orderId", orderId)
					.build();
			boolean accepted = stateMachine.sendEvent(message);
			if (!accepted) {
				log.warn("Event {} was not accepted for order {}", event, orderId);
			}
			return accepted;
		});
	}

	private <T> T withStateMachine(String orderId, Function<StateMachine<OrderStatus, SagaEvent>, T> callback) {
		StateMachine<OrderStatus, SagaEvent> stateMachine = stateMachineService.acquireStateMachine(orderId);
		stateMachine.addStateListener(listenerFactory.create(orderId));
		try {
			stateMachine.startReactively().block();
			return callback.apply(stateMachine);
		} catch (Exception exception) {
			throw new IllegalStateException("Could not drive saga for order %s".formatted(orderId), exception);
		} finally {
			try {
				stateMachine.stopReactively().block();
			} catch (Exception stopException) {
				log.warn("Failed stopping state machine for order {}", orderId, stopException);
			}
			stateMachineService.releaseStateMachine(orderId);
		}
	}
}
