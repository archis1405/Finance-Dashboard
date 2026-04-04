# Finance Dashboard with Access Control

## Functional Requirements

The functionalities implemented are : 
- JWT access tokens and refresh-token rotation
- Logout support with refresh-token revocation
- Separate `roles` and `permissions` tables
- Business-unit scoping for non-admin access
- Audit fields on records and audit-log persistence
- Richer record filters and keyword search
- CSV and Excel export endpoints
- Dashboard response caching
- Flyway database migrations
- Swagger / OpenAPI documentation
- Request logging and simple rate limiting
- Scheduled daily and monthly dashboard snapshots
- Expanded integration tests
- MySQL-only datasource configuration

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security
- Flyway
- MySQL
- JWT (`jjwt`)
- Springdoc OpenAPI
- Apache POI
- Maven Wrapper

## Security Model

Authentication:

- `POST /api/auth/login` returns access and refresh tokens
- `POST /api/auth/refresh` rotates refresh tokens
- `POST /api/auth/logout` revokes the supplied refresh token

Authorization:

- Users belong to one role and one business unit
- Roles are mapped to permissions in the database
- `ADMIN` includes cross-business-unit access
- `VIEWER` and `ANALYST` are restricted to their own business unit

## Demo Accounts

- `viewer` / `viewer123` in `FIN-OPS`
- `analyst` / `analyst123` in `FIN-OPS`
- `admin` / `admin123` in `FIN-OPS`
- `growth_analyst` / `growth123` in `GROWTH`

## Business Units

- `FIN-OPS`
- `GROWTH`

## Running the Project

1. Ensure MySQL is running locally on port `3306`.
2. Update credentials in [application.properties]
3. Start the app:

```bash
./mvnw spring-boot:run
```

## Database Notes

- Flyway migration initializes the schema from `src/main/resources/db/migration/V1__init_schema.sql`
- JPA runs with `ddl-auto=validate`
- The default datasource is MySQL and the app auto-creates the `finance_dashboard` database if missing

## API Overview

Base path: `/api`

### Auth

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

### Users

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/status`

### Financial records

- `POST /api/records`
- `GET /api/records`
- `GET /api/records/{id}`
- `PUT /api/records/{id}`
- `DELETE /api/records/{id}`
- `GET /api/records/export.csv`
- `GET /api/records/export.xlsx`

### Dashboard

- `GET /api/dashboard/summary`
- `GET /api/dashboard/trends/monthly`
- `GET /api/dashboard/recent-activity`
- `GET /api/dashboard/snapshots/monthly`

## Filtering and Search

`GET /api/records` supports:

- `type`
- `categories`
- `startDate`
- `endDate`
- `minAmount`
- `maxAmount`
- `keyword`
- `page`
- `size`
- `sort`

## API Documentation 
Link : Link to the API docs : https://docs.google.com/document/d/1uuvkU75XnZvd8VaHkOEUgSEci_rgKs96RvVZJlnALqc/edit?usp=sharing

## Operational Features

- Request logging for all API traffic
- In-memory per-endpoint rate limiting
- Scheduled daily and monthly summary snapshots
- Cached dashboard summary responses
- Soft-delete for financial records
- Export support for reporting workflows

## Assumptions

- The assignment keeps a single role per user, but permissions are normalized into separate tables.
- `ADMIN` has cross-business-unit access; other roles are unit-scoped.
- Dashboard summaries and trends exclude soft-deleted records.
- Refresh tokens are persisted in hashed form.
- Scheduled snapshots are generated daily and monthly and exposed through the dashboard API.
- MySQL is the only supported database for this project.

## Important Note : 
```
NOTE : Please find the postman export attached , directly use the import for testing the APIs
```

```
THANK YOU and I hope to hear back soon 
```