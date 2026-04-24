# Backend Assessment - Spring Boot Redis Guardrail Service

This project is a Spring Boot microservice built for the Backend Engineering Assignment.
It acts as the central API layer and guardrail system for posts, comments, likes, Redis-based virality tracking, concurrency-safe bot limits, and notification batching.

---

## Tech Stack

* Java 17+
* Spring Boot 3.x
* PostgreSQL
* Redis
* Docker Compose

---

## Approach

The system follows a clear separation of responsibilities:

* PostgreSQL stores persistent business data such as posts and comments.
* Redis manages fast-changing distributed state such as virality scores, bot counters, cooldowns, and pending notifications.

All concurrency-sensitive rules are enforced in Redis **before** database writes.
This ensures correctness under high load and prevents race conditions.

---

## Features

* Create posts
* Add comments to posts
* Like posts
* Real-time virality scoring using Redis
* Bot interaction guardrails (horizontal, vertical, cooldown limits)
* Notification batching and summarization

---

## REST Endpoints

### Create Post

**POST** `/api/posts`
Creates a new post and returns the created entity.

### Add Comment

**POST** `/api/posts/{postId}/comments`
Adds a comment while enforcing depth limits and bot guardrails.

### Like Post

**POST** `/api/posts/{postId}/like`
Registers a like and updates the virality score.

---

## Error Handling

* `429 Too Many Requests` → Bot limit or cooldown exceeded
* `400 Bad Request` → Invalid depth level
* `404 Not Found` → Invalid post ID

---

## Redis Virality Engine

Virality score is updated in Redis in real time for each interaction.

Scoring rules:

* Bot Reply → +1
* Human Like → +20
* Human Comment → +50

Example key:

```
post:{id}:virality_score
```

---

## Atomic Locks and Thread Safety

Redis is used as the single source of truth for concurrency control.

### Horizontal Cap

A post cannot receive more than 100 bot replies.

* Redis key: `post:{id}:bot_count`
* Uses atomic `INCR`
* If count exceeds 100 → request rejected with `429`

### Vertical Cap

A comment thread cannot exceed depth level 20.

* Requests with `depthLevel > 20` are rejected before persistence

### Cooldown Cap

A bot cannot interact with the same human more than once in 10 minutes.

* Redis key: `cooldown:bot_{id}:human_{id}`
* Uses TTL-based locking

### Thread Safety Guarantee

All guardrail checks are enforced using Redis atomic operations **before any database write**.
Since Redis operations are atomic, concurrent requests cannot bypass limits.

This guarantees:

* Exact enforcement of the 100 bot reply cap under concurrent load
* No race conditions
* No invalid database writes

---

## Stateless Design

The application is fully stateless.

* No in-memory counters
* No static variables
* No local caching for guardrails

All runtime state is stored in Redis.

---

## Notification Engine

Bot notifications are throttled and batched using Redis:

* If user is outside cooldown → immediate notification
* If inside cooldown → stored in Redis list

Example:

```
user:{id}:pending_notifs
```

---

## Scheduled Sweeper

A scheduled task runs every 5 minutes (for testing):

* Aggregates pending notifications
* Logs summary:

  ```
  Bot X and N others interacted with your posts
  ```
* Clears processed notifications

---

## How to Run

### 1. Start infrastructure

```bash
docker-compose up -d
```

### 2. Run the application

```bash
mvn spring-boot:run
```

### 3. Application will be available at

```
http://localhost:8080
```

---

## Postman Collection

* Import: `postman_collection.json`
* Ensure backend is running on: `http://localhost:8080`
* Set variable:

  ```
  base_url = http://localhost:8080
  ```

### Recommended Execution Order

1. Create Post
2. Add Comment
3. Like Post

---

## Project Structure

```
project-root/
├── src/
├── docker-compose.yml
├── pom.xml
├── README.md
├── postman_collection.json
```

---

## Repository Contents

* Spring Boot source code
* `docker-compose.yml` for PostgreSQL and Redis
* Postman collection for API testing
* This README explaining system design and thread safety

---

## Testing Coverage

This implementation satisfies:

* Concurrent bot reply protection
* Exact horizontal cap enforcement (100 bot replies)
* Vertical depth validation
* Bot-human cooldown enforcement
* Stateless architecture
* Data integrity using Redis guardrails before DB writes

---
