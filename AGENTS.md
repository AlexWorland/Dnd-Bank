# Repository Guidelines

## Project Structure & Module Organization
The Spring Boot app lives under `src/main/java/com/dndbank/bank`, grouped by concern: `entity`, `repository`, `service`, `controller`, `dto`, `support`, and `config`. Thymeleaf templates are in `src/main/resources/templates` (split into `auth/`, `player/`, `dm/`), with shared styles in `static/css/`. Database migrations are handled via JPA auto-DDL for now; keep schema-critical helpers inside `support/`. Scripts such as the database backup entrypoint live under `scripts/` and are mounted by Docker.

## Build, Test, and Development Commands
```bash
./gradlew bootRun             # run the API + UI locally
docker compose up --build     # launch app, Postgres, and backup worker
./gradlew test                # execute unit/integration tests when added
```
Use `.env` or shell exports to override `SPRING_DATASOURCE_*` and `APP_DEFAULT_DM_*` variables. The Docker stack persists Postgres in the `pgdata` volume and writes `./backups/*.sql` snapshots every 30 minutes.

## Coding Style & Naming Conventions
Target Java 17 with Spring Boot 3. Use constructor injection, annotate services/controllers sparingly, and keep domain logic in services rather than controllers. Repository methods should be descriptive (`findByOwner`, `totalBankHoldingsCopper`). Currency math uses copper-piece longs—never store money as floating point.

## Testing Guidelines
Adopt Spring Boot test slices plus Mockito for service-level scenarios. Focus on banking flows: deposits, withdrawals, transfers, and DM overrides. When currency math is involved, assert on raw copper totals and the formatted output from `CurrencyFormatter`.

## Commit & Pull Request Guidelines
Follow the conventional commits pattern (`feat(accounts): add Zelle transfer`). Each PR should explain player vs. DM impact, list test evidence (`./gradlew test`, docker smoke checks), and include UI screenshots when templates change. Link campaign/backlog issues and wait for CI to pass before merging.

## Currency & Security Notes
All balances and transactions are copper-based longs. Use `MoneyAmountForm` for user input and `CurrencyUtils`/`CurrencyFormatter` for conversions (PP/GP/EP/SP/CP). Treat DM onboarding secrets as credentials—keep them in env vars or secret stores, never in commits. The backup container writes plaintext dumps to `./backups`; prune or encrypt them if they contain sensitive campaign data.
