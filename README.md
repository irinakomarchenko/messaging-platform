# Messaging Platform

Real-time messaging platform based on microservices architecture.

## Overview

Messaging Platform is a backend system for real-time text communication.

The system supports:

- one-to-one chats;
- group chats;
- named channels;
- online message delivery;
- offline message synchronization;
- user presence: ONLINE / OFFLINE.

## Architecture

The project is organized as a microservices application in a monorepo.

Each service is a separate Spring Boot application with its own responsibility.

## Services

| Service | Responsibility |
|---|---|
| api-gateway-service | Entry point, routing, future rate limiting and auth validation |
| user-service | Registration, login, user profile, username search |
| group-channel-service | Group chats, named channels, members and subscribers |
| chat-history-service | Store and read message history, missed messages synchronization |
| messaging-service | WebSocket connections and real-time message delivery |
| connection-management-service | Online/offline status, connection mapping and heartbeat |

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven
- REST API
- WebSocket
- MapStruct
- Docker Compose later
- PostgreSQL / Redis / Kafka / NoSQL later