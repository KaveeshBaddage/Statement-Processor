# Bank Statement Processor

A Spring Boot application that retrieves monthly bank statement data from an external banking API, calculates account totals, and submits the resulting summary to an external API.

The project follows a **Contract-First Development** approach and is implemented using **Ports & Adapters (Hexagonal) Architecture** to maintain a clear separation between business logic and external integrations.

---

## Business Goal

The objective of this application is to process monthly bank statement data obtained from an external banking service.

For a given account and month, the application:

1. Retrieves all statement transactions from the external Bank API.
2. Aggregates transactions for the requested month.
3. Calculates:
  - Total income
  - Total spending
  - Monthly balance
4. Sends the calculated monthly summary to an external Summary API.
5. Returns the processing result to the caller.

The application does **not** maintain a persistence layer or store transaction data. All processing is performed in memory using data retrieved from external services.

### Processing Flow

```mermaid
flowchart TD

    A["Client Request"] --> B["Statement Processor"]

    B -->|"1. Fetch monthly statements"| C["External Bank API"]
    C -->|"2. Return statement data"| B

    B --> D["Calculate Monthly Summary"]
    D --> E["Income"]
    D --> F["Spending"]
    D --> G["Balance"]

    E --> H["Submit Monthly Summary"]
    F --> H
    G --> H

    H -->|"3. POST summary"| I["External Summary API"]
    I -->|"4. Return response"| H

    H --> B
    B --> J["Return API Response"]
```

---

## Overview

The application acts as an orchestration layer between external systems.

Consumers initiate processing for a specific account and month. The application retrieves all required statement data, performs the necessary calculations, and submits the resulting summary to a downstream service.

Technical details of external integrations such as pagination, HTTP communication, serialization, and vendor-specific API contracts are hidden from application consumers.

---

## Contract-First Development

The integrations with the external APIs are developed using a contract-first approach.

An OpenAPI specification acts as the source of truth for the external API contract. Java client code is generated automatically during the Maven build process.

### Generation Flow

```text
bank-statement-api.yaml
          │
          │ OpenAPI Generator
          ▼
┌──────────────────────────────────────────┐
│ Generated Bank HTTP Client               │
│                                          │
│ client.bank.api                          │
│   └── StatementsApi.java                 │
│                                          │
│ client.bank.model                        │
│   ├── MonthlyStatement.java              │
│   ├── Transaction.java                   │
│   └── Problem.java                       │
│                                          │
│ client.bank.invoker                      │
│   └── ApiClient.java                     │
└──────────────────────────────────────────┘
          │
          ▼
      Bank API
```

### Generated Client Configuration

During the `generate-sources` Maven phase:

- The OpenAPI specification (`bank-statement-api.yaml`) is processed by OpenAPI Generator.
- A Java HTTP client is generated using Spring's `RestClient`.
- Generated sources are written to:

```text
target/generated-sources/bank-statement
```

Generated code is organized into:

```text
client.bank.api
client.bank.model
client.bank.invoker
```

The generated client uses:

- Jakarta APIs
- Jackson for JSON serialization/deserialization

### Generate the Client

```bash
./mvnw generate-sources
```

---

## Application API

The application exposes an endpoint that triggers the monthly statement processing workflow.

### Request Monthly Summary Processing

```http
POST /api/v1/accounts/{accountId}/monthly-summary/{month}
```

#### Example

```http
POST /api/v1/accounts/account-12345/monthly-summary/2026-10
```

### Design Considerations


#### Request Pagination

The external banking API exposes paginated statement data.

This implementation deliberately hides pagination from application consumers. The application is responsible for retrieving all pages required for the requested month, aggregating the results, performing the calculations, and submitting the final summary.


#### Observability

Implemented request correlation and logging to improve traceability and troubleshooting.

-  Generates a UUID when the incoming **X-Request-ID** header is missing; otherwise, reuses the provided ID.

- Returns the request ID in the response header and stores it in MDC for log correlation.

- Propagates X-Request-ID to downstream APIs via generated OpenAPI clients.

- Logs key processing events, transaction counts, summary calculation, and downstream submission status.

This enables easier request tracking across service boundaries and simplifies debugging.

#### Rate Limiting

Rate limiting is implemented to protect the application from excessive requests and maintain stability under heavy load.

- Resource Protection: Limits incoming requests to prevent excessive memory and CPU consumption.

- Application Stability: Reduces the risk of resource exhaustion because monthly summary processing is performed in application memory.

- Controlled Processing: Restricts the number of requests allowed within a configured time period using Resilience4j.

- HTTP 429 Response: Returns 429 Too Many Requests when the configured rate limit is exceeded.

- Improved Reliability: Helps maintain consistent performance and reliable processing during traffic spikes.


---

## Architecture

### Ports & Adapters (Hexagonal Architecture)

The application is implemented using **Ports & Adapters Architecture**.

The business logic resides at the center of the application and communicates with external systems exclusively through well-defined ports.

```text
                ┌────────────────────┐
                │   REST Controller  │
                └─────────┬──────────┘
                          │
                    Inbound Port
                          │
                          ▼
                ┌────────────────────┐
                │  Application Core  │
                │   Business Logic   │
                └────────────────────┘
                          ▲
                          │
                   Outbound Port
                          │
         ┌────────────────┴────────────────┐
         │                                 │
         ▼                                 ▼
  Bank API Adapter              Summary API Adapter
```

### Benefits

- Clear separation of concerns
- Business logic independent of frameworks and infrastructure
- Easier unit and integration testing
- Reduced coupling to external systems
- Easier replacement of adapters and technologies
- Improved maintainability and extensibility

---

## Working Without a Real API

A real banking API may not be available during early development.

To avoid blocking progress:

- API contracts are defined first using OpenAPI.
- HTTP clients are generated from the contracts.
- Adapters can be implemented against mocks, stubs, or WireMock servers.
- Business logic can be developed and tested independently of external providers.

This enables parallel development while maintaining a stable integration contract.

---

## Testing

The project is tested at multiple levels.

### Unit Tests

MonthlySummaryCalculatorTest  & ProcessMonthlyStatementUseCaseTest

- Monthly balance calculations
- Income aggregation
- Spending aggregation
- Application services

### Slice Tests

MonthlySummaryControllerTest is a Spring MVC slice test (@WebMvcTest) that verifies 

- controller mappings
- request validation
- response serialization
- exception handling 

while mocking the application use case layer. It is not a full integration test because the business logic and external dependencies are mocked.

### Integration Tests

- REST API endpoints
- External API adapters
- OpenAPI-generated client integration

### Contract Tests

Contract tests have not been included in the current implementation phase. They may be introduced in a future phase to verify API compatibility between services and ensure that changes to API contracts do not unintentionally break integrations.

Benefits of future implementation:

- Detects API contract mismatches between consumers and providers.

- Reduces integration issues when services evolve independently.

- Improves confidence in API changes without relying solely on end-to-end testing.

- Supports safer and more reliable service integration.

---

## Deployment

The application can be packaged and deployed as a containerized Spring Boot service.

Typical deployment options include:

- Docker
- Kubernetes
- Azure Kubernetes Service (AKS)
- Amazon EKS
- Google Kubernetes Engine (GKE)

Recommended deployment pipeline:

1. Build and execute tests.
2. Generate OpenAPI client sources.
3. Package the application.
4. Build a Docker image.
5. Push the image to a container registry.
6. Deploy using Kubernetes manifests or Helm charts.

Configuration should be externalized through environment variables or a configuration management solution.

---

## Persistence

No persistence layer is used in this solution.

The application does not store transactions, statements, or summaries in a database. Data is retrieved from external services, processed in memory, and forwarded to the target API.