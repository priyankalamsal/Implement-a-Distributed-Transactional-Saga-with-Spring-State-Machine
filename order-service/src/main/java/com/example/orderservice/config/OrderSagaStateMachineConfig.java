package com.example.orderservice.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;
import com.example.orderservice.service.OrderTransitionActionService;

@Configuration
@EnableStateMachineFactory
public class OrderSagaStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatus, SagaEvent> {

	private final StateMachineRuntimePersister<OrderStatus, SagaEvent, String> stateMachineRuntimePersister;
	private final OrderTransitionActionService transitionActionService;

	public OrderSagaStateMachineConfig(
			StateMachineRuntimePersister<OrderStatus, SagaEvent, String> stateMachineRuntimePersister,
			OrderTransitionActionService transitionActionService) {
		this.stateMachineRuntimePersister = stateMachineRuntimePersister;
		this.transitionActionService = transitionActionService;
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<OrderStatus, SagaEvent> config) throws Exception {
		config.withPersistence().runtimePersister(stateMachineRuntimePersister);
	}

	@Override
	public void configure(StateMachineStateConfigurer<OrderStatus, SagaEvent> states) throws Exception {
		states.withStates()
				.initial(OrderStatus.ORDER_CREATED)
				.states(EnumSet.allOf(OrderStatus.class));
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<OrderStatus, SagaEvent> transitions) throws Exception {
		transitions.withExternal()
				.source(OrderStatus.ORDER_CREATED)
				.target(OrderStatus.PAYMENT_PENDING)
				.event(SagaEvent.CREATE_ORDER)
				.action(transitionActionService::onPaymentPending)
			.and()
				.withExternal()
				.source(OrderStatus.PAYMENT_PENDING)
				.target(OrderStatus.PAYMENT_COMPLETED)
				.event(SagaEvent.PAYMENT_SUCCESS)
				.action(transitionActionService::onPaymentCompleted)
			.and()
				.withExternal()
				.source(OrderStatus.PAYMENT_PENDING)
				.target(OrderStatus.ORDER_FAILED)
				.event(SagaEvent.PAYMENT_FAILED)
				.action(transitionActionService::onPaymentFailed)
			.and()
				.withExternal()
				.source(OrderStatus.PAYMENT_COMPLETED)
				.target(OrderStatus.INVENTORY_RESERVED)
				.event(SagaEvent.INVENTORY_SUCCESS)
				.action(transitionActionService::onInventoryReserved)
			.and()
				.withExternal()
				.source(OrderStatus.PAYMENT_COMPLETED)
				.target(OrderStatus.ORDER_FAILED)
				.event(SagaEvent.INVENTORY_FAILED)
				.action(transitionActionService::onInventoryFailed)
			.and()
				.withExternal()
				.source(OrderStatus.INVENTORY_RESERVED)
				.target(OrderStatus.ORDER_COMPLETED)
				.event(SagaEvent.COMPLETE_ORDER)
				.action(transitionActionService::onOrderCompleted);
	}
}
