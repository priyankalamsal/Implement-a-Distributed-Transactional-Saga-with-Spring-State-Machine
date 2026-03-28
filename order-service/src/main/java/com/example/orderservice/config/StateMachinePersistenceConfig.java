package com.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;

@Configuration
public class StateMachinePersistenceConfig {

	@Bean
	public StateMachineRuntimePersister<OrderStatus, SagaEvent, String> stateMachineRuntimePersister(
			JpaStateMachineRepository jpaStateMachineRepository) {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}

	@Bean
	public StateMachineService<OrderStatus, SagaEvent> stateMachineService(
			StateMachineFactory<OrderStatus, SagaEvent> stateMachineFactory,
			StateMachineRuntimePersister<OrderStatus, SagaEvent, String> stateMachineRuntimePersister) {
		return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
	}
}
