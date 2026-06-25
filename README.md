# PixelTech ERP System

The enterprise application monitored by **DevOps Commander** (the AI-103 multi-agent project).
It is a real, working ERP with three modules — **Customers**, **Products/Inventory**, and **Orders** — built to be deployed across Azure VMs and AWS EC2 (production + development) with a dedicated MySQL database server per environment.

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Bean Validation, Actuator + Micrometer/Prometheus |
| Frontend | React 18 + Vite, served by nginx |
| Database | MySQL 8 (H2 in-memory for local dev) |
| Packaging | Docker (multi-stage) + docker compose |

## Why it's built this way

- **Actuator + Prometheus metrics** (`/actuator/health`, `/actuator/prometheus`) so Datadog (prod) and Grafana (dev) can monitor it.
- **Chaos endpoints** (`/api/chaos/*`) let us deliberately break the app so the AI agents have real incidents to detect and investigate.
- **Separate DB server** matches the production topology (app server and database on different hosts).

## Run locally (no Docker)

Backend (uses in-memory H2, no database needed):

```bash
cd backend
mvn spring-boot:run
# API on http://localhost:8080  (health: /actuator/health)
```

Frontend:

```bash
cd frontend
npm install
npm run dev
# UI on http://localhost:5173  (proxies /api to :8080)
```

## Run the full stack with Docker

```bash
docker compose up --build
# Frontend:  http://localhost:8081
# Backend:   http://localhost:8080
# MySQL:     localhost:3306
```

## API summary

| Method | Path | Purpose |
|--------|------|---------|
| GET/POST | `/api/customers` | List / create customers |
| DELETE | `/api/customers/{id}` | Delete a customer |
| GET/POST | `/api/products` | List / create products |
| GET | `/api/products/low-stock` | Products at/below threshold |
| GET/POST | `/api/orders` | List / place orders (deducts stock) |
| PUT | `/api/orders/{id}/status` | Change order status |
| GET | `/api/dashboard/stats` | Aggregated counts + revenue |
| GET | `/actuator/health` | Health probe |
| GET | `/actuator/prometheus` | Metrics for monitoring |

## Incident simulation (for the AI demo)

```bash
# Start failures (subsequent /api/chaos/maybe-fail calls return HTTP 500)
curl -X POST http://localhost:8080/api/chaos/enable-failures

# Simulate a latency spike (sleeps 5s)
curl "http://localhost:8080/api/chaos/slow?ms=5000"

# Recover
curl -X POST http://localhost:8080/api/chaos/disable-failures
```

## Repositories

- Application code: `https://github.com/PixelTech-Solutions/ERP_System.git`
- Infrastructure (Terraform): `https://github.com/PixelTech-Solutions/ERP_Infra.git`
