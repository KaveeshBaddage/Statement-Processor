# contract-first development

OpenAPI YAML → OpenAPI Generator → generated Java HTTP client →  service uses the client

When Maven reaches generate-sources, take bank-statement-api.yaml, generate a Java HTTP client using the RestClient implementation, put the generated files under target/generated-sources/bank-statement, put API classes in the api package, models in the model package, HTTP infrastructure in the invoker package, use Jakarta APIs, and use Jackson for JSON.

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

generate the http clients

```
./mvnw generate-sources 
```


application API

User wants to submit account summery based on month

So this application exposes following API Endpoint to send internal account id along with desired month. 

POST /api/v1/accounts/{accountId}/monthly-summary/{month}

For example:
POST /api/v1/accounts/account-12345/monthly-summary/2026-10

This application doesn't expose the third-party Bank API's pagination to application consumer.


* What problem does this application try to solve?



* What kind of architecture was used to achieve the solution?

This application is build based on  Ports & Adapters Architecture. All input and output reaches/leaves the application through a port that isolates the application from external tools, technologies and delivery mechanisms.
Using this port/adapter design, application in the centre of the system, allows to keep the application isolated from the business logic like delivery mechanisms  and tools used by the system, making it easier and faster to test and to create a reusable proof of concept.


* How to test your application?

* If the application must be deployed to a server in a remote location, how would you do it?

* There is no real API to connect to. Think about how you can start implementing your solution before
  other parties are ready with their implementations.


