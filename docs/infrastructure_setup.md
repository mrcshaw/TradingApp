# Trading Application Infrastructure & Configuration

This document outlines the administrative configurations, connection management, and infrastructure integrations for the microservices within the Trading App project.

## 1. Connection Management

### API Gateway (Netty)
The API Gateway is built on **Spring Cloud Gateway**, which utilizes **Netty** (a non-blocking, asynchronous, event-driven network application framework) under the hood. 
- **Why?** It allows the Gateway to keep thousands of client connections open simultaneously without allocating a dedicated thread for each request. It efficiently streams data and routes it to the downstream services.

### Microservices (Tomcat)
The individual microservices (e.g., `account-service`, `trading-service`) utilize **Spring Boot Embedded Tomcat**. 
- **Why?** They handle synchronous REST API operations. Each service embeds its own Tomcat server (listening on separate ports, e.g., 8081, 8082), eliminating the need for a standalone monolithic application server.

### Database Connection Pooling (HikariCP)
Database interactions in the microservices (like `account-service`) are managed by **HikariCP**, the default, high-performance JDBC connection pool in Spring Boot.
- **Why?** Instead of opening and closing database connections for every query, HikariCP maintains a pool of open connections to PostgreSQL. This drastically reduces connection overhead and latency.

## 2. Rate Limiting (Redis)
To protect the services from DDoS attacks and to manage API quotas, the API Gateway implements a **Token Bucket Rate Limiter** using **Redis**.

- **Infrastructure:** A Redis 7 container runs alongside PostgreSQL via `docker-compose.yml`.
- **Implementation:** The gateway uses `RedisRateLimiter` configured with a specific replenish rate and burst capacity. 
- **Resolution:** Limits are currently resolved by the client's IP Address using a `KeyResolver`. Later, this can be swapped out to rate-limit based on a user's authenticated JWT token.

## 3. Secrets Management (HashiCorp Vault)
Hardcoding secrets (database passwords, API keys) is a security risk. The platform uses **HashiCorp Vault** to securely store and inject these credentials at runtime.

- **Dev Server:** Vault is run locally using `vault server -dev -dev-root-token-id="root" -dev-listen-address="127.0.0.1:8200"`.
- **Injection:** Spring Boot uses the `spring-cloud-starter-vault-config` dependency. During startup, the application reaches out to Vault, authenticates using its token, and pulls configurations (like `spring.datasource.username` and `spring.datasource.password`). These are injected directly into the application context in memory without ever touching the disk.

## Building Javadocs
Official Javadocs have been added to the codebase. To generate the navigable HTML documentation for any service, navigate to that service's directory and run:

```bash
mvn javadoc:javadoc
```
The output will be located in the `target/site/apidocs` directory of the respective service.