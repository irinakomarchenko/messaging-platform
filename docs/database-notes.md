# Database Notes

## Current Decision

The project uses real infrastructure databases from the beginning.

The first version uses PostgreSQL for relational data instead of H2.

## Storage by Service

| Service | Current Storage | Future Evolution |
|---|---|---|
| user-service | PostgreSQL | PostgreSQL |
| group-channel-service | PostgreSQL | PostgreSQL / NoSQL for large membership data |
| chat-history-service | PostgreSQL | Cassandra / ScyllaDB for high-scale message history |
| connection-management-service | Redis later | Redis / Key-Value DB |
| messaging-service | No persistent database | Kafka/NATS/RabbitMQ integration later |
| api-gateway-service | No database | Redis for rate limiting later |

## Principle

Each microservice owns its own data.

Other services must not access another service database directly.

All communication must go through service APIs.

## Development Approach

For local development, each service can have its own PostgreSQL database or its own PostgreSQL container.

For production-like architecture, databases should be isolated per service boundary.