# Architecture

## Architecture Style

The system is designed as a microservices application in a monorepo.

Each service is an independent Spring Boot application.

## High-Level Services

- API Gateway Service
- User Service
- Group Channel Service
- Chat History Service
- Messaging Service
- Connection Management Service

## Storage Strategy

The first version uses H2 databases for stateful services.

Later evolution:

- PostgreSQL for users and metadata;
- Redis for presence and connection mapping;
- Kafka / NATS / RabbitMQ for asynchronous delivery;
- Cassandra / ScyllaDB / DynamoDB-like storage for chat history.