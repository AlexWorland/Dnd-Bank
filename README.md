# DND Bank

A Spring Boot banking system tailored for tabletop campaigns. The DM controls institutions and NPCs, while players manage deposits, withdrawals, and instant transfers from any device.

## Running with Docker

```bash
docker compose up --build
```

Services:
- `app`: Spring Boot service (port 8080)
- `db`: PostgreSQL with persistent named volume `pgdata`
- `db-backup`: hourly `pg_dump` into `./backups`

The default DM credentials come from `APP_DEFAULT_DM_*` environment variables (see `docker-compose.yml`).

## Development Scripts

- `mvn spring-boot:run`
- Configure datasource via `SPRING_DATASOURCE_*` env vars (see `src/main/resources/application.yml`).

## Key Features

- Server-rendered, mobile-friendly dashboard for both DM and players (Thymeleaf templates under `src/main/resources/templates`).
- Role-based security via Spring Security (username/password).
- Postgres persistence using Spring Data JPA entities for users, institutions, accounts, and transactions.
- Instant, Zelle-style transfers validated by account number.
- Automated backups retained for 7 days under `./backups/`.
- Currency-aware accounting: balances are stored in copper pieces and rendered as Platinum/Gold/Electrum/Silver/Copper so the UI always speaks in D&D coinage.

## Currency Model

- Domain services store every balance and transaction as copper-piece longs to avoid floating point drift.
- User-facing forms (player + DM) collect PP/GP/EP/SP/CP counts via `MoneyAmountForm`, and `CurrencyFormatter` renders friendly strings like `2 pp 4 gp 3 sp`.
- When extending banking logic, always interact with the copper representation and convert at the edges only.
# Dnd-Bank
