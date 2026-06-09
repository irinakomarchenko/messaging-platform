# API Contracts

## Overview

This document describes the first version of REST API contracts for the Messaging Platform.

The system is implemented as a microservices application in a monorepo.
Each service is a separate Spring Boot application.

The first implementation uses H2 databases inside stateful services.
The API is designed so that storage and infrastructure can later be replaced with PostgreSQL, Redis, Kafka/NATS/RabbitMQ and NoSQL storage.

## API Design Principles

1. REST API is used for request/response operations:

    * registration;
    * login;
    * user search;
    * creating groups;
    * creating channels;
    * joining channels;
    * reading message history;
    * syncing missed messages.

2. WebSocket is used for real-time messaging:

    * sending messages;
    * receiving messages;
    * delivery acknowledgements;
    * heartbeat;
    * online/offline events.

3. Each service owns its own data.

4. Services communicate through explicit APIs.

5. The first version may use synchronous REST calls between services.
   Later, delivery-related flows can be moved to Kafka, NATS or RabbitMQ.

---

# Services

## API Gateway Service

### Responsibility

The API Gateway Service is the entry point for clients.

In the first version it can be added later or kept simple.
In a production-like version it is responsible for:

* routing;
* authentication validation;
* rate limiting;
* request logging;
* correlation IDs;
* TLS termination;
* basic DDoS protection.

### Planned Routes

| External Path              | Target Service                |
| -------------------------- | ----------------------------- |
| `/api/v1/auth/**`          | user-service                  |
| `/api/v1/users/**`         | user-service                  |
| `/api/v1/groups/**`        | group-channel-service         |
| `/api/v1/channels/**`      | group-channel-service         |
| `/api/v1/conversations/**` | chat-history-service          |
| `/api/v1/messages/**`      | chat-history-service          |
| `/api/v1/connections/**`   | connection-management-service |
| `/api/v1/presence/**`      | connection-management-service |
| `/ws/**`                   | messaging-service             |

---

# User Service API

## Responsibility

The User Service is responsible for:

* user registration;
* login;
* user profile;
* username search;
* basic authentication response;
* later JWT integration.

Initial storage: H2.
Future storage: PostgreSQL.

---

## POST `/api/v1/auth/signup`

Registers a new user.

### Request

```json
{
  "fullName": "Irina Komarchenko",
  "username": "irina",
  "password": "StrongPassword123"
}
```

### Response `201 Created`

```json
{
  "userId": 1,
  "username": "irina",
  "authToken": "temporary-token-for-mvp"
}
```

### Validation Rules

| Field      | Rule                              |
| ---------- | --------------------------------- |
| `fullName` | required, 2–100 characters        |
| `username` | required, unique, 3–50 characters |
| `password` | required, minimum 8 characters    |

### Possible Errors

| Status            | Reason                  |
| ----------------- | ----------------------- |
| `400 Bad Request` | Invalid request body    |
| `409 Conflict`    | Username already exists |

---

## POST `/api/v1/auth/login`

Authenticates a user.

### Request

```json
{
  "username": "irina",
  "password": "StrongPassword123"
}
```

### Response `200 OK`

```json
{
  "userId": 1,
  "username": "irina",
  "authToken": "temporary-token-for-mvp"
}
```

### Possible Errors

| Status             | Reason                       |
| ------------------ | ---------------------------- |
| `400 Bad Request`  | Invalid request body         |
| `401 Unauthorized` | Invalid username or password |

---

## GET `/api/v1/users/{userId}`

Returns user profile.

### Response `200 OK`

```json
{
  "id": 1,
  "fullName": "Irina Komarchenko",
  "username": "irina",
  "createdAt": "2026-06-08T12:00:00Z"
}
```

### Possible Errors

| Status          | Reason         |
| --------------- | -------------- |
| `404 Not Found` | User not found |

---

## GET `/api/v1/users/search?username=irina`

Searches user by username.

### Response `200 OK`

```json
{
  "id": 1,
  "fullName": "Irina Komarchenko",
  "username": "irina"
}
```

### Possible Errors

| Status          | Reason         |
| --------------- | -------------- |
| `404 Not Found` | User not found |

---

# Group Channel Service API

## Responsibility

The Group Channel Service is responsible for:

* group chats;
* named channels;
* group members;
* channel subscribers;
* membership validation;
* subscriber lookup for delivery.

Initial storage: H2.
Future storage: PostgreSQL or NoSQL depending on scale.

---

## POST `/api/v1/groups`

Creates a group chat.

### Request

```json
{
  "name": "Backend Team",
  "ownerId": 1,
  "memberIds": [2, 3]
}
```

### Response `201 Created`

```json
{
  "groupId": 100,
  "name": "Backend Team",
  "ownerId": 1,
  "memberIds": [1, 2, 3],
  "createdAt": "2026-06-08T12:00:00Z"
}
```

### Notes

The owner is automatically added as a group member.

### Possible Errors

| Status            | Reason                          |
| ----------------- | ------------------------------- |
| `400 Bad Request` | Invalid request body            |
| `404 Not Found`   | One of the users does not exist |

---

## POST `/api/v1/groups/{groupId}/members`

Adds a member to a group.

### Request

```json
{
  "userId": 4
}
```

### Response `200 OK`

```json
{
  "groupId": 100,
  "userId": 4,
  "status": "ADDED"
}
```

### Possible Errors

| Status          | Reason                   |
| --------------- | ------------------------ |
| `404 Not Found` | Group or user not found  |
| `409 Conflict`  | User is already a member |

---

## GET `/api/v1/groups/{groupId}/members`

Returns group members.

### Response `200 OK`

```json
{
  "groupId": 100,
  "memberIds": [1, 2, 3, 4]
}
```

---

## POST `/api/v1/channels`

Creates a named channel.

### Request

```json
{
  "name": "java-backend",
  "ownerId": 1
}
```

### Response `201 Created`

```json
{
  "channelId": 200,
  "name": "java-backend",
  "slug": "java-backend",
  "channelUrl": "/channels/java-backend",
  "ownerId": 1,
  "createdAt": "2026-06-08T12:00:00Z"
}
```

### Possible Errors

| Status            | Reason                      |
| ----------------- | --------------------------- |
| `400 Bad Request` | Invalid request body        |
| `409 Conflict`    | Channel name already exists |

---

## POST `/api/v1/channels/{channelId}/subscribe`

Subscribes user to a channel.

### Request

```json
{
  "userId": 2
}
```

### Response `200 OK`

```json
{
  "channelId": 200,
  "userId": 2,
  "status": "SUBSCRIBED"
}
```

### Possible Errors

| Status          | Reason                     |
| --------------- | -------------------------- |
| `404 Not Found` | Channel or user not found  |
| `409 Conflict`  | User is already subscribed |

---

## GET `/api/v1/channels/{channelId}/subscribers`

Returns channel subscribers.

### Response `200 OK`

```json
{
  "channelId": 200,
  "subscriberIds": [1, 2, 3, 4]
}
```

### Production Note

For very large channels, returning all subscribers at once is not scalable.
Later this endpoint should support pagination or segmentation.

Example:

```text
GET /api/v1/channels/{channelId}/subscribers?limit=1000&cursor=...
```

---

# Chat History Service API

## Responsibility

The Chat History Service is responsible for:

* storing messages;
* reading message history;
* missed messages synchronization;
* message ordering inside a conversation;
* idempotency by `clientMessageId`.

Initial storage: H2.
Future storage: Cassandra, ScyllaDB or DynamoDB-like storage.

---

## POST `/api/v1/messages`

Stores a message.

This endpoint is usually called by `messaging-service`, not directly by the client.

### Request

```json
{
  "clientMessageId": "client-msg-123",
  "conversationId": "private-1-2",
  "conversationType": "PRIVATE_CHAT",
  "senderId": 1,
  "text": "Hello"
}
```

### Response `201 Created`

```json
{
  "serverMessageId": 1000,
  "clientMessageId": "client-msg-123",
  "conversationId": "private-1-2",
  "conversationType": "PRIVATE_CHAT",
  "senderId": 1,
  "text": "Hello",
  "createdAt": "2026-06-08T12:00:00Z",
  "status": "STORED"
}
```

### Conversation Types

```text
PRIVATE_CHAT
GROUP_CHAT
CHANNEL
```

### Possible Errors

| Status            | Reason                                      |
| ----------------- | ------------------------------------------- |
| `400 Bad Request` | Invalid request body                        |
| `409 Conflict`    | Duplicate `clientMessageId` for this sender |

---

## GET `/api/v1/conversations/{conversationId}/messages?limit=50`

Returns recent messages from a conversation.

### Response `200 OK`

```json
{
  "conversationId": "private-1-2",
  "messages": [
    {
      "serverMessageId": 1000,
      "senderId": 1,
      "text": "Hello",
      "createdAt": "2026-06-08T12:00:00Z"
    }
  ]
}
```

### Notes

Default limit: `50`.
Maximum limit: `100`.

---

## GET `/api/v1/conversations/{conversationId}/messages/sync?afterMessageId=1000`

Returns missed messages after a known message id.

### Response `200 OK`

```json
{
  "conversationId": "private-1-2",
  "afterMessageId": 1000,
  "messages": [
    {
      "serverMessageId": 1001,
      "senderId": 2,
      "text": "Hi",
      "createdAt": "2026-06-08T12:01:00Z"
    }
  ]
}
```

### Purpose

This endpoint is used after reconnect.

The client sends the last known message id, and the server returns only messages created after that id.

---

# Connection Management Service API

## Responsibility

The Connection Management Service is responsible for:

* ONLINE / OFFLINE status;
* connection mapping;
* active WebSocket sessions;
* heartbeat timestamp;
* last seen time.

Initial storage: H2 or in-memory.
Future storage: Redis / Key-Value DB.

---

## POST `/api/v1/connections/connect`

Registers active WebSocket connection.

This endpoint is usually called by `messaging-service`.

### Request

```json
{
  "userId": 1,
  "serverId": "messaging-service-1",
  "connectionId": "ws-abc-123",
  "deviceId": "web"
}
```

### Response `200 OK`

```json
{
  "userId": 1,
  "status": "ONLINE",
  "serverId": "messaging-service-1",
  "connectionId": "ws-abc-123"
}
```

---

## POST `/api/v1/connections/disconnect`

Marks connection as disconnected.

### Request

```json
{
  "userId": 1,
  "connectionId": "ws-abc-123"
}
```

### Response `200 OK`

```json
{
  "userId": 1,
  "status": "OFFLINE",
  "lastSeenAt": "2026-06-08T12:30:00Z"
}
```

---

## POST `/api/v1/connections/heartbeat`

Updates heartbeat timestamp.

### Request

```json
{
  "userId": 1,
  "connectionId": "ws-abc-123"
}
```

### Response `200 OK`

```json
{
  "userId": 1,
  "connectionId": "ws-abc-123",
  "status": "ALIVE"
}
```

---

## GET `/api/v1/presence/users/{userId}`

Returns user presence.

### Response `200 OK`

```json
{
  "userId": 1,
  "status": "ONLINE",
  "lastSeenAt": "2026-06-08T12:30:00Z"
}
```

---

## GET `/api/v1/connections/users/{userId}`

Returns active connections for a user.

### Response `200 OK`

```json
{
  "userId": 1,
  "connections": [
    {
      "serverId": "messaging-service-1",
      "connectionId": "ws-abc-123",
      "deviceId": "web",
      "status": "ONLINE"
    }
  ]
}
```

---

# Messaging Service API

## Responsibility

The Messaging Service is responsible for:

* WebSocket connections;
* receiving messages from clients;
* storing messages through Chat History Service;
* checking recipients through Group Channel Service;
* checking online connections through Connection Management Service;
* delivering messages to online users;
* sending acknowledgements.

The main client-facing API of this service is WebSocket.

REST endpoints can be added for health checks and internal diagnostics.

---

## GET `/api/v1/messaging/health`

Returns service status.

### Response `200 OK`

```json
{
  "service": "messaging-service",
  "status": "UP"
}
```

---

# Common Error Response

All services should return a consistent error format.

```json
{
  "timestamp": "2026-06-08T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request body",
  "path": "/api/v1/auth/signup",
  "validationErrors": {
    "username": "Username is required"
  }
}
```

---

# Implementation Order

The services should be implemented in this order:

1. `user-service`
2. `group-channel-service`
3. `chat-history-service`
4. `connection-management-service`
5. `messaging-service`
6. `api-gateway-service`

Reason:

* `user-service` is required by almost all other services.
* `group-channel-service` defines recipients for groups and channels.
* `chat-history-service` stores messages.
* `connection-management-service` stores online status.
* `messaging-service` coordinates real-time delivery.
* `api-gateway-service` can be added after the core services exist.
