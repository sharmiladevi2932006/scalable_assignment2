# Order Workflow: CQS, CQRS, Idempotency, Retry and Compensation

A runnable Spring Boot example backed by an in-memory H2 database.

## Run

Requirements: Java 17+ and Maven 3.9+.

```powershell
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.

## Try the workflow

Successful order, because quantity is within available inventory:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/orders -Headers @{'X-Event-Id'='order-event-1'} -ContentType 'application/json' -Body '{"productId":101,"quantity":1,"amount":1500}'
```

Expected status: `CONFIRMED`.

Compensation order, because this example intentionally fails inventory reservation for quantities greater than 3:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/orders -Headers @{'X-Event-Id'='order-event-2'} -ContentType 'application/json' -Body '{"productId":101,"quantity":5,"amount":7500}'
```

Expected status: `CANCELLED`, after three inventory retries and a payment refund.

Sending the same `X-Event-Id` again returns the original order instead of creating a duplicate.

## Query endpoints

- `GET http://localhost:8080/orders`
- `GET http://localhost:8080/orders/{id}`
- `POST http://localhost:8080/orders/{id}/reconcile`

## CQS endpoints

The combined command/query service is available separately for comparison:

- `POST http://localhost:8080/cqs/orders`
- `GET http://localhost:8080/cqs/orders/{id}`

## H2 console

Open `http://localhost:8080/h2-console` and use:

- JDBC URL: `jdbc:h2:mem:ordersdb`
- User: `sa`
- Password: leave blank

The CQS example is in `OrderCqsService`. The split command and query services are `OrderCommandService` and `OrderQueryService`.
