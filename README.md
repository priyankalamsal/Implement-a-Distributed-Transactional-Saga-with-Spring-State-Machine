# Fault-Tolerant Order Management System

This repository contains a three-service Spring Boot implementation of the Saga pattern for distributed order processing:

- `order-service`: the saga orchestrator backed by Spring State Machine and PostgreSQL persistence
- `payment-service`: a deterministic payment mock that can succeed, fail, or delay by `orderId`
- `inventory-service`: a deterministic inventory mock that can succeed or fail by `orderId`

## What It Demonstrates

- Saga orchestration with Spring State Machine
- Persisted state machine runtime context in PostgreSQL
- Deterministic downstream failures for compensation testing
- Recovery of in-flight sagas after an `order-service` restart
- Containerized local execution with Docker Compose

## Project Layout

- `docker-compose.yml`
- `.env.example`
- `submission.json`
- `order-service/`
- `payment-service/`
- `inventory-service/`

## Quick Start

1. Copy `.env.example` to `.env`.
2. Run `docker compose up --build -d`.
3. Check health with `docker compose ps`.

## Order API

### Create Order

`POST http://localhost:8080/api/orders`

Example body:

```json
{
  "orderId": "100",
  "customerId": 1,
  "productId": 1,
  "quantity": 2,
  "unitPrice": 50.0
}
```

`orderId` is optional for general use, but providing it is the supported way to trigger the deterministic scenarios defined in `submission.json`.

### Get Order Status

`GET http://localhost:8080/api/orders/{orderId}`

## Deterministic Scenarios

`submission.json` and `.env.example` are aligned around these values:

- Success: `100`
- Payment failure: `201`
- Inventory failure: `302`

### Successful Saga

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"100","customerId":1,"productId":1,"quantity":2,"unitPrice":50.0}'
```

After a few seconds:

```bash
curl http://localhost:8080/api/orders/100
```

Expected status: `ORDER_COMPLETED`

### Payment Failure

Use `orderId` `201`. Expected final status: `ORDER_FAILED`

### Inventory Failure With Compensation

Use `orderId` `302`. Expected final status: `ORDER_FAILED`, and `payment-service` logs should show both `process` and `cancel`.

## Restart Recovery Scenario

Set `PAYMENT_DELAY_MS=10000` in `.env`, rebuild, and then:

1. Create a successful order.
2. Restart only the orchestrator while payment is still pending:
   `docker compose restart order-service`
3. Query the order after the restart.

The order should still finish as `ORDER_COMPLETED` because the saga state and order status are persisted and resumed on startup.

## Observability

The `order-service` logs every saga transition in the format:

`Saga for order {orderId} transitioning from {sourceState} to {targetState} on event {event}.`

Useful commands:

- `docker logs order-service`
- `docker logs payment-service`
- `docker logs inventory-service`

## Notes

- The order service accepts an additional internal completion event to move from `INVENTORY_RESERVED` to `ORDER_COMPLETED`, because the task specification does not include a separate public completion event.
- Payment and inventory are intentionally stateless mocks so the saga logic stays centered in the orchestrator.
# Implement-a-Distributed-Transactional-Saga-with-Spring-State-Machine
# Implement-a-Distributed-Transactional-Saga-with-Spring-State-Machine
# Implement-a-Distributed-Transactional-Saga-with-Spring-State-Machine
