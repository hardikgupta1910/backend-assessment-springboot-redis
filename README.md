# Backend Assessment - Spring Boot Redis Guardrail Service

This project is a Spring Boot microservice built for the Backend Engineering Assignment. The service acts as the central API layer and guardrail system for handling posts, comments, likes, Redis-based counters, cooldowns, and notification batching [file:2].

## Tech Stack

- Java 17+
- Spring Boot 3.x
- PostgreSQL
- Redis
- Docker Compose [file:2]

## Objective

The goal of this project is to build a robust and stateless backend service that can:

- Create posts
- Add comments
- Like posts
- Maintain a Redis-based virality score
- Enforce concurrency-safe atomic guardrails for bot interactions
- Batch bot notifications using Redis and scheduled jobs [file:2]

## Features Implemented

- `POST /api/posts` to create a post
- `POST /api/posts/{postId}/comments` to add a comment
- `POST /api/posts/{postId}/like` to like a post
- Redis virality score calculation
- Horizontal bot reply cap
- Vertical depth cap
- Bot-human cooldown cap
- Redis notification throttling
- Scheduled notification sweeper every 5 minutes [file:2]

## Database Schema

The project uses the following entities:

- User
- Bot
- Post
- Comment [file:2]

PostgreSQL stores the actual persistent records for posts and comments. Redis stores temporary distributed state such as virality scores, bot reply counters, cooldown keys, and pending notifications [file:2].

## REST Endpoints

### Create Post
**POST** `/api/posts`

Sample request:
```json
{
  "authorId": 1,
  "content": "Fresh post for testing"
}
```

### Add Comment
**POST** `/api/posts/{postId}/comments`

Sample human comment request:
```json
{
  "authorId": 2,
  "content": "This is a human comment",
  "depthLevel": 1,
  "bot": false,
  "humanId": 2
}
```

Sample bot comment request:
```json
{
  "authorId": 101,
  "content": "This is a bot reply",
  "depthLevel": 1,
  "bot": true,
  "humanId": 2
}
```

### Like Post
**POST** `/api/posts/{postId}/like` [file:2]

## Redis Virality Engine

Virality score is updated in Redis in real time based on user interaction type:

- Bot Reply = +1
- Human Like = +20
- Human Comment = +50 [file:2]

A Redis key such as `post:{id}:virality_score` is updated instantly whenever an interaction happens [file:2].

## Atomic Locks and Thread Safety

Thread safety was guaranteed using Redis atomic operations before allowing bot interactions [file:2].

### 1. Horizontal Cap
A single post cannot have more than 100 bot replies. This is enforced using a Redis counter:

```text
post:{id}:bot_count
```

The service uses an atomic Redis increment operation. If the value exceeds 100, the request is rejected with HTTP 429 Too Many Requests [file:2].

### 2. Vertical Cap
A comment thread cannot go deeper than 20 levels. Any request with `depthLevel > 20` is rejected [file:2].

### 3. Cooldown Cap
A specific bot cannot interact with a specific human more than once within 10 minutes. This is enforced with a Redis TTL key:

```text
cooldown:bot_{id}:human_{id}
```

If the cooldown key already exists, the interaction is blocked [file:2].

### Why this is thread-safe
Redis atomic operations were used as the gatekeeper instead of Java in-memory counters or shared objects [file:2]. This ensures that concurrent requests are checked consistently even under heavy load, which is required for the spam test described in the assignment [file:2].

## Statelessness

The application is fully stateless as required by the assignment [file:2]. No `HashMap`, static variables, or in-memory counters were used for guardrails, cooldowns, or notifications [file:2]. All such runtime state is stored in Redis [file:2].

## Data Integrity

PostgreSQL acts as the source of truth for actual stored data, while Redis acts as the gatekeeper before writes are committed [file:2]. Database transactions are allowed only after Redis guardrails pass, which prevents invalid comments from being persisted [file:2].

## Notification Engine

To avoid notification spam from bot interactions, the following logic was implemented:

- If a user has not received a notification in the last 15 minutes, an immediate notification is logged and a Redis cooldown key is set.
- If the user is still in the cooldown window, the notification message is pushed to a Redis list:
  `user:{id}:pending_notifs` [file:2]

## Scheduled Sweeper

A Spring `@Scheduled` task runs every 5 minutes to simulate production notification batching [file:2]. It scans users with pending notifications, summarizes the batched messages, logs the summary, and clears the pending Redis list [file:2].

## Testing Summary

The following scenarios were tested successfully:

- Create post
- Human comment
- Bot comment
- Like post
- Vertical cap rejection
- Cooldown rejection
- Horizontal cap stopping exactly at 100 bot replies
- Notification queueing
- Scheduled notification summary logging [file:2]

## How to Run the Project

### 1. Start Redis and PostgreSQL
```bash
docker-compose up -d
```

### 2. Run the Spring Boot application
```bash
mvn spring-boot:run
```

### 3. Test endpoints
Use the attached Postman collection JSON to test all APIs and edge cases [file:2].

## Deliverables

This repository contains:

- Spring Boot source code
- `docker-compose.yml`
- Postman collection JSON
- README file [file:2]
