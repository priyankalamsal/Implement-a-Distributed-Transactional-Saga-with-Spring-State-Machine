package com.example.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.SagaEvent;

@Component
public class OrderStateMachineListenerFactory {

	public StateMachineListener<OrderStatus, SagaEvent> create(String orderId) {
		Logger log = LoggerFactory.getLogger("SagaTransitionLogger");
		return new StateMachineListenerAdapter<>() {
			@Override
			public void transition(Transition<OrderStatus, SagaEvent> transition) {
				if (transition == null || transition.getSource() == null || transition.getTarget() == null) {
					return;
				}
				String source = transition.getSource().getId() == null ? "UNKNOWN" : transition.getSource().getId().name();
				String target = transition.getTarget().getId() == null ? "UNKNOWN" : transition.getTarget().getId().name();
				String event = transition.getTrigger() != null && transition.getTrigger().getEvent() != null
						? transition.getTrigger().getEvent().name()
						: "AUTO";
				log.info("Saga for order {} transitioning from {} to {} on event {}.", orderId, source, target, event);
			}

			@Override
			public void stateMachineError(StateMachine<OrderStatus, SagaEvent> stateMachine, Exception exception) {
				log.error("State machine error for order {}", orderId, exception);
			}
		};
	}
}
