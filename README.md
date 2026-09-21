# Scalable Systems - Spring Boot Programs

This repository contains two combined Spring Boot projects based on the concepts covered in Chapter 4. The projects demonstrate transaction handling, process orchestration, failure handling, and consistency techniques.

## Project Structure

```text
scalable_assignment2/
│
├── Prg_1-5/
│   └── Combined Spring Boot Project
│
└── Prg_6-10/
    └── Combined Spring Boot Project
```

## Prg_1-5

This is a single Spring Boot project combining Programs 1 to 5. It demonstrates an order processing workflow involving transactions, inventory, payment, compensation, and Saga orchestration.

### Concepts Covered

* **Local ACID Transaction** – Ensures operations within a local transaction are handled atomically.
* **Business Invariant** – Ensures inventory stock does not become negative.
* **Payment & Compensation** – Handles payment and refunds when a later operation fails.
* **Saga Orchestration** – Coordinates the order, payment, and inventory steps.
* **REST Controller** – Provides an API endpoint for creating orders.

### Workflow

```text
Create Order → Payment → Reserve Inventory → Confirm Order
```

If inventory reservation fails, the payment is refunded and the order is cancelled.

## Prg_6-10

This is a single Spring Boot project combining Programs 6 to 10. It demonstrates separation of responsibilities and techniques for handling failures and repeated processing.

### Concepts Covered

* **CQS** – Separates operations that modify data from operations that read data.
* **CQRS** – Separates command and query responsibilities into different services.
* **Idempotency** – Prevents duplicate events from producing duplicate effects.
* **Retry** – Attempts a failed operation again for transient failures.
* **Reconciliation** – Identifies and checks workflows that remain incomplete.

## Technologies Used

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* Maven
