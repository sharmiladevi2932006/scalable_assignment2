# Order-Saga-Project

## 1. Purpose

This is a college-level Spring Boot demo that combines five small, related
concepts into one runnable application built around placing an order:

1. A **local ACID transaction**
2. A **business invariant** (stock can never go negative)
3. A **payment step with compensation** (refund)
4. A **Saga orchestration** that ties everything together
5. A **REST controller** that exposes it all over HTTP

Together they simulate — in a simplified, single-service way — how a
distributed **Saga pattern** coordinates multiple local operations and
rolls back ("compensates") earlier steps when a later step fails.

## 2. Technologies Used

| Technology          | Version |
|---------------------|---------|
| Java                 | 21 |
| Spring Boot          | 4.1.0 |
| Spring Web           | (Spring Boot starter) |
| Spring Data JPA      | (Spring Boot starter) |
| H2 Database          | file-based, persistent |
| Maven                | build tool |

## 3. Project Structure

```
Order-Saga-Project/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/example/ordersaga/
        │       ├── OrderSagaApplication.java
        │       ├── config/
        │       │   └── DataInitializer.java       (seeds sample products)
        │       ├── controller/
        │       │   └── OrderController.java        (Program 5)
        │       ├── entity/
        │       │   ├── Order.java
        │       │   ├── OrderStatus.java
        │       │   └── Product.java
        │       ├── exception/
        │       │   ├── GlobalExceptionHandler.java
        │       │   ├── InvalidOrderRequestException.java
        │       │   └── ProductNotFoundException.java
        │       ├── repository/
        │       │   ├── OrderRepository.java
        │       │   └── ProductRepository.java
        │       └── service/
        │           ├── OrderService.java            (Program 1)
        │           ├── InventoryService.java         (Program 2)
        │           ├── PaymentService.java            (Program 3)
        │           └── SagaService.java                (Program 4)
        └── resources/
            └── application.properties
```

## 4. Explanation of Programs 1–5

### Program 1 — Local ACID Transaction (`OrderService`)
`createOrder()` is annotated `@Transactional`. It sets the order's status
to `CREATED` and saves it through `OrderRepository` as a single, local,
atomic database operation.

### Program 2 — Business Invariant (`InventoryService`)
The invariant is: **stock must never become negative.**
- `reserveStock(productId, quantity)` looks up the product, throws
  `ProductNotFoundException` if it doesn't exist, returns `false` if
  stock is insufficient, otherwise decrements stock and saves it.
- `restoreStock(productId, quantity)` gives stock back and is used for
  compensation when a later Saga step fails.

### Program 3 — Payment and Compensation (`PaymentService`)
`makePayment(amount)` simulates a payment gateway using console output
(e.g. `Payment successful: Rs.1500`). `refund(amount)` simulates undoing
that payment (`Payment refunded: Rs.1500`). No real payment gateway is
involved — this is intentional for a classroom project.

### Program 4 — Saga Orchestration (`SagaService`)
`placeOrder(order)` is the main workflow. It deliberately does **not**
wrap everything in one big `@Transactional` method — instead it performs
a sequence of separate local operations, exactly like a real Saga would
across independent services:

1. Create the order → `CREATED`
2. Make payment → if it fails, set `PAYMENT_FAILED` and stop
3. Payment succeeded → set `PAID`
4. Reserve inventory
5. If reservation fails → **compensate**: refund the payment, set
   `CANCELLED`
6. If reservation succeeds → set `CONFIRMED`

### Program 5 — REST Controller (`OrderController`)
Exposes `POST /orders`, accepts an `Order` as JSON via `@RequestBody`,
delegates to `SagaService.placeOrder()`, and returns the resulting order
(with its final status) as JSON.

## 5. Database Details

H2 is configured as a **persistent, file-based** database so data
survives application restarts, while still being effortless for local
development (no server to install).

- JDBC URL: `jdbc:h2:file:./data/ordersaga`
- Username: `sa`
- Password: *(empty)*
- `ddl-auto`: `update` (tables are created/updated automatically from the
  JPA entities)
- Data file location: `./data/ordersaga.mv.db`, relative to wherever you
  run the app from

## 6. How to Open in VS Code

1. Install the **Extension Pack for Java** and the **Spring Boot
   Extension Pack** from the VS Code marketplace (if not already
   installed).
2. `File → Open Folder…` and select the `Order-Saga-Project` folder.
3. VS Code will detect the `pom.xml` and set up the Maven project
   automatically.

## 7. How to Run Using Maven

From the `Order-Saga-Project` folder:

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

Alternatively, build a runnable jar and run it directly:

```bash
mvn clean package
java -jar target/ordersaga-1.0.0.jar
```

## 8. How to Access the H2 Console

1. With the app running, open **http://localhost:8080/h2-console** in a
   browser.
2. Use these connection settings:
   - **JDBC URL:** `jdbc:h2:file:./data/ordersaga`
   - **User Name:** `sa`
   - **Password:** *(leave blank)*
3. Click **Connect**.
4. Run `SELECT * FROM ORDERS;` and `SELECT * FROM PRODUCT;` to inspect
   the data.

## 9. H2 JDBC URL

```
jdbc:h2:file:./data/ordersaga
```

## 10. Sample Data

On first startup, `DataInitializer` seeds two products (only if they
don't already exist, so restarting the app never creates duplicates):

| Product ID | Name     | Stock |
|-----------:|----------|------:|
| 101        | Laptop   | 10    |
| 102        | Keyboard | 5     |

## 11. Sample POST Request

**Successful order:**

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 1, "amount": 1500}'
```

**Expected successful response** (stock for product 101 drops from 10
to 9):

```json
{
  "id": 1,
  "productId": 101,
  "quantity": 1,
  "amount": 1500.0,
  "status": "CONFIRMED"
}
```

**Inventory-failure / compensation example** (quantity greater than
available stock):

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 50, "amount": 75000}'
```

**Expected failure/compensation response** (payment is made, then
refunded because there isn't enough stock; product stock is left
unchanged and never goes negative):

```json
{
  "id": 2,
  "productId": 101,
  "quantity": 50,
  "amount": 75000.0,
  "status": "CANCELLED"
}
```

**Invalid product example:**

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 999, "quantity": 1, "amount": 500}'
```

Returns an HTTP `404` with a clean JSON error body instead of a stack
trace, e.g.:

```json
{
  "timestamp": "2026-09-09T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999"
}
```

## 12. Complete Saga Flow

```
POST /orders
      |
      v
Create Order (status = CREATED)
      |
      v
Make Payment
      |
   +--+--+
   |     |
  NO    YES
   |     |
   v     v
PAYMENT_ PAID
FAILED    |
          v
   Reserve Inventory
          |
       +--+--+
       |     |
      NO    YES
       |     |
       v     v
   Refund   CONFIRMED
   Payment
       |
       v
   CANCELLED
```

## 13. Troubleshooting

- **Port 8080 already in use** — stop whatever else is using it, or run
  with a different port: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`
- **`/h2-console` returns a 404 / "No static resource h2-console"** — as
  of Spring Boot 4.0, auto-configuration was split into smaller modules,
  and the H2 console's auto-configuration moved into its own artifact
  (`org.springframework.boot:spring-boot-h2console`). Having the H2
  driver on the classpath and `spring.h2.console.enabled=true` set is no
  longer enough by itself — this project's `pom.xml` already declares
  that dependency, so if you hit this, double check it wasn't removed
  and re-run `mvn clean spring-boot:run`.
- **H2 console shows "Database not found" / connection fails** — make
  sure the JDBC URL you type into the H2 console exactly matches
  `application.properties` (`jdbc:h2:file:./data/ordersaga`) and that
  you're connecting while the app is running (or, if the app isn't
  running, connect from the same working directory so the relative
  `./data` path resolves correctly).
- **Stale build errors in VS Code** — run `mvn clean compile` from the
  terminal, then reload the VS Code window (`Developer: Reload Window`)
  so the Java language server picks up the change.
- **Data file locked ("Database may be already in use")** — this
  happens if two instances of the app try to open the same H2 file at
  once. Stop any other running instance first.
- **Changes to entities not reflected in the schema** — `ddl-auto` is
  set to `update`, which adds new columns but does not remove old ones
  or handle every kind of structural change. For a clean slate during
  development, stop the app and delete the `data/` folder, then restart.
