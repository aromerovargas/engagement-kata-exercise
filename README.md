# Engagement Kata Exercise

A Spring Boot service for managing firms, customers, and product templates, including versioning of product templates and diffing between versions.

## Requirements

- Java 25 (JDK)
- Maven (or use the bundled `mvnw` wrapper — no local Maven install required)

## How to run it

From the project root:

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080` (default Spring Boot port; no custom port is configured in `application.properties`).

To build and run the packaged jar instead:

```bash
./mvnw clean package
java -jar target/engagement-kata-0.0.1-SNAPSHOT.jar
```

## How to test it

### Unit tests

Run the test suite with:

```bash
./mvnw test
```

### Manual test via curl

With the application running, add a new version to product template `1`:

```bash
curl -X POST http://localhost:8080/api/v1/product-templates/1/versions \
 -H "Content-Type: application/json" \
 -d '{"name":"Basic Template","version":"2.0","content":"{\"fields\":[\"title\",\"description\",\"price\"]}"}'
```

Expected response:
- `201 Created` if the version did not exist yet and was created.
- `200 OK` if the version already existed.

## Workflow

Adding a new product template version triggers a small asynchronous pipeline, decoupled through two in-memory Spring Integration queue channels:

1. `ProductTemplateController` receives the `POST /api/v1/product-templates/{id}/versions` request and delegates to `AddNewProductTemplateVersion`, which persists the new version (skipping it idempotently if it already exists with the same content) and publishes a `ProductTemplateUpdatedEvent` onto the `updated_product_template_queue` channel.
2. `NewProductTemplateVersionProcessor` consumes that event, looks up every firm/customer combination still using an older version of the template via `FindCustomersByProductTemplate`, and publishes one `EngagementFilesSummaryUpdateMessage` per firm/version pair onto the `update_engagement_files_summary_queue` channel.
3. `EngagementFilesSummaryUpdateProcessor` consumes each of those messages to refresh the affected engagement files summary (currently just logs the update).

Each stage only depends on the channel and the message/event payload, so producers and consumers can be tested and evolved independently.

## Thread management

Both queues are backed by bounded `QueueChannel`s (capacity 100, see `MessageQueuesConfig`), which decouple the producing thread from the consuming thread:

- The HTTP request thread only publishes to `updated_product_template_queue` and returns immediately; it never blocks on downstream processing.
- `EngagementFilesSummaryUpdateProcessor` is polled by a dedicated `summaryTaskExecutor` thread pool (2 threads, prefix `summary-`) on a fixed 1-second delay, isolating engagement summary processing from the rest of the application.
- `NewProductTemplateVersionProcessor` is driven by Spring Integration's default poller/task scheduler, since no dedicated executor is configured for `updated_product_template_queue`.

Because both channels have a bounded capacity, a slow consumer applies backpressure to its producer (publishing blocks once the queue is full) instead of growing memory usage without limit.
