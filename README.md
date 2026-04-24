# Backend Assessment - Spring Boot Redis Guardrail Service

This project is a Spring Boot microservice built for the Backend Engineering Assignment. It acts as the central API layer and guardrail system for posts, comments, likes, Redis-based virality tracking, concurrency-safe bot guardrails, and notification batching .

## Tech Stack

- Java 17+
- Spring Boot 3.x
- PostgreSQL
- Redis
- Docker Compose 

## Approach

The application is designed with PostgreSQL as the persistent source of truth and Redis as the real-time guardrail layer.
PostgreSQL stores actual entities such as posts and comments, while Redis handles fast-changing distributed state like virality score, bot reply counters, cooldown keys, and pending notifications.

The main idea is simple:
- Persist actual business data in PostgreSQL.
- Enforce concurrency-sensitive rules in Redis before committing database changes.
- Keep the application stateless by storing all runtime counters and cooldowns in Redis instead of Java memory .

## Features

- Create a new post
- Add comments to a post
- Like a post
- Calculate virality score in Redis
- Enforce bot interaction guardrails
- Batch and summarize bot notifications using Redis and scheduled tasks 

## Database Schema

The service is based on the following entities:
- User
- Bot
- Post
- Comment 

PostgreSQL stores the actual content and relationships, while Redis stores temporary operational state required for throttling, counting, and concurrency control .

## REST Endpoints

### Create Post
**POST** `/api/posts`

### Add Comment
**POST** `/api/posts/{postId}/comments`

### Like Post
**POST** `/api/posts/{postId}/like` 

## Redis Virality Engine

Virality score is updated in Redis in real time whenever a user interacts with a post.

Scoring rules:
- Bot Reply = +1
- Human Like = +20
- Human Comment = +50 

A Redis key such as `post:{id}:virality_score` is incremented based on the interaction type.

## Atomic Locks and Thread Safety

The assignment requires Redis-based atomic operations to guarantee thread-safe enforcement of Phase 2 guardrails.

### Horizontal Cap
A single post cannot receive more than 100 bot replies. This is enforced using a Redis counter such as:

```text
post:{id}:bot_count
```

Before saving a bot comment, the service performs an atomic Redis increment. If the updated counter exceeds 100, the request is rejected with HTTP 429 Too Many Requests and the bot comment is not committed to the database.

### Vertical Cap
A comment thread cannot go deeper than 20 levels. Any request with `depthLevel > 20` is rejected before persistence.

### Cooldown Cap
A specific bot cannot interact with the same human more than once in 10 minutes. This is enforced using a Redis TTL key such as:

```text
cooldown:bot_{id}:human_{id}
```

If the cooldown key already exists, the interaction is blocked.

### How thread safety was guaranteed
Thread safety was guaranteed by using Redis atomic operations as the gatekeeper for bot interactions instead of Java in-memory state. Since Redis operations such as `INCR` and key existence checks are atomic at the Redis level, concurrent requests cannot bypass the horizontal cap or cooldown logic through race conditions in application memory.

This directly addresses the assignment’s spam test, where 200 concurrent requests may attempt to create bot replies at the same time and the system must stop exactly at 100 accepted replies. Database writes are performed only after Redis guardrails pass, which protects data integrity and prevents invalid extra comments from being stored.

## Statelessness

The application is fully stateless as required by the assignment. No `HashMap`, static counters, or in-memory Java collections are used for virality tracking, cooldowns, atomic locks, or pending notifications. All such distributed runtime state is stored in Redis.

## Notification Engine

Bot notifications are throttled and batched using Redis.

- If a user has not received a bot notification in the last 15 minutes, an immediate notification event is logged and a cooldown key is created.
- If the user is already inside the cooldown window, the notification is appended to a Redis list such as `user:{id}:pending_notifs`.

## Scheduled Sweeper

A Spring `@Scheduled` task runs every 5 minutes for testing and simulates periodic notification batching. It scans users with pending Redis notifications, summarizes them, logs a message such as `Bot X and [N] others interacted with your posts`, and clears the list afterward.

## How to Run

### 1. Start PostgreSQL and Redis
```bash
docker-compose up -d
```

### 2. Run the Spring Boot application
```bash
mvn spring-boot:run
```

## Project Structure

```text
backend-assessment-springboot-redis/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── pom.xml
├── README.md
└── Backend_Assessment_Postman_Collection.json
```

## Repository Contents

This repository contains:
- The Spring Boot source code
- `docker-compose.yml` to run PostgreSQL and Redis locally
- Postman collection JSON for API testing
- This README explaining the implementation approach and thread safety strategy for Atomic Locks in Phase 2

## Testing Notes

The implementation is intended to satisfy the following assignment scenarios:
- Concurrent bot reply protection
- Exact horizontal cap enforcement at 100 bot replies
- Vertical depth validation
- Bot-human cooldown enforcement
- Stateless runtime design
- PostgreSQL data integrity with Redis gatekeeping
