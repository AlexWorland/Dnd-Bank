# DND Bank

A Spring Boot banking system tailored for tabletop campaigns. The DM controls institutions and NPCs, while players manage deposits, withdrawals, and instant transfers from any device.

## Quick Start

### Running with Docker

```bash
docker compose up --build
```

This starts three services:
- **`app`**: Spring Boot service running on port 8080
- **`db`**: PostgreSQL 16 database with persistent volume `pgdata`
- **`backup`**: Automated backup service that runs `pg_dump` every 30 minutes (configurable via `BACKUP_INTERVAL_SECONDS`)

The application will be available at `http://localhost:8080`.

### Default Credentials

The default DM credentials are configured via environment variables in `docker-compose.yml`:
- Username: `dm` (configurable via `APP_DEFAULT_DM_USERNAME`)
- Password: `ChangeMe123!` (configurable via `APP_DEFAULT_DM_PASSWORD`)
- Display Name: `Dungeon Master` (configurable via `APP_DEFAULT_DM_DISPLAY_NAME`)

**Important**: Change the default DM password in production environments!

### Environment Variables

Key environment variables you can override:

- `POSTGRES_USER` / `POSTGRES_PASSWORD`: Database credentials (default: `dnd_master`)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `APP_DEFAULT_DM_USERNAME` / `APP_DEFAULT_DM_PASSWORD`: Default DM account credentials
- `APP_DEFAULT_DM_DISPLAY_NAME`: Default DM display name
- `APP_DEFAULT_INSTITUTION_NAME`: Default bank institution name (default: `Gilded Griffon Bank`)
- `BACKUP_INTERVAL_SECONDS`: Backup frequency in seconds (default: `1800` = 30 minutes)

## Development

### Local Development

Run the application locally without Docker:

```bash
./gradlew bootRun
```

Configure the datasource via `SPRING_DATASOURCE_*` environment variables or update `src/main/resources/application.yml`.

### Testing

```bash
./gradlew test
```

Tests use Spring Boot test slices and Mockito. Code coverage reports are generated via JaCoCo.

### Project Structure

```
src/main/java/com/dndbank/bank/
├── config/          # Spring configuration (Security, Database)
├── controller/      # Web controllers (Auth, Dashboard, DM, Player)
├── dto/            # Data transfer objects for forms
├── entity/         # JPA entities (User, Account, AccountTransaction, BankInstitution)
├── enums/          # Domain enums (Role, AccountType, TransactionType)
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic services
└── support/        # Utilities (Currency formatting, Account number generation)

src/main/resources/
├── templates/      # Thymeleaf templates
│   ├── auth/      # Login and registration pages
│   ├── dm/        # DM dashboard
│   ├── player/    # Player dashboard
│   └── fragments/ # Shared template fragments
└── static/css/    # Stylesheets
```

## Key Features

- **Server-rendered UI**: Mobile-friendly Thymeleaf templates for both DM and players
- **Role-based Security**: Spring Security with username/password authentication
- **PostgreSQL Persistence**: Spring Data JPA with auto-DDL for schema management
- **Instant Transfers**: Zelle-style transfers validated by account number
- **Automated Backups**: PostgreSQL dumps saved to `./backups/` and retained for 7 days
- **D&D Currency System**: Full support for Platinum/Gold/Electrum/Silver/Copper coinage

## Currency Model

The application uses a copper-piece-based currency system to avoid floating-point precision issues:

- **Storage**: All balances and transactions are stored as `long` values representing copper pieces
- **Input**: User-facing forms (`MoneyAmountForm`) collect PP/GP/EP/SP/CP counts
- **Display**: `CurrencyFormatter` renders friendly strings like `2 pp 4 gp 3 sp`
- **Conversion**: `CurrencyUtils` handles conversions between coin types

**Important**: When extending banking logic, always work with copper-piece `long` values internally and convert at the UI boundaries only.

## Database

The application uses PostgreSQL with JPA auto-DDL (`hibernate.ddl-auto: update`). The database persists in a Docker volume (`pgdata`) and is automatically backed up to `./backups/` every 30 minutes. Backups older than 7 days are automatically cleaned up.

## Technology Stack

- **Java 21** with Spring Boot 3.2.5
- **PostgreSQL 16** for persistence
- **Thymeleaf** for server-side rendering
- **Spring Security** for authentication and authorization
- **Gradle** for build management
- **Docker Compose** for containerized deployment

## Contributing

Follow the conventional commits pattern for commit messages (e.g., `feat(accounts): add Zelle transfer`). When submitting PRs:

- Include test coverage for new features
- Update documentation as needed
- Test with `./gradlew test` and Docker smoke checks
- Include UI screenshots for template changes
