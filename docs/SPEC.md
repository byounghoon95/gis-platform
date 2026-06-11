# GIS B2B Location Analysis Platform Spec

## 1. Project Summary

GIS 기반 B2B 입지 분석 플랫폼.

후보 매장 위치를 지도에 등록하고, 교통 접근성, 주변 수요시설, 경쟁 강도, 유동인구, 임대료를 기준으로 입지 점수를 계산한다. 사용자는 지도에서 후보지를 비교하고, 후보지별 분석 근거를 리포트 형태로 확인할 수 있다.

## 2. Target Job Fit

이 프로젝트는 다음 역량을 보여주는 것을 목표로 한다.

- Java, Spring Boot 기반 REST API 개발
- React 기반 B2B 관리 화면 구현
- Google Maps API 기반 지도 시각화
- PostGIS 기반 반경 검색 및 공간 데이터 처리
- Docker Compose 기반 로컬 실행 환경 구성
- 설명 가능한 rule-based scoring 설계
- Git 기반 협업과 문서화
- AI 코딩 도구 활용 경험 정리

## 3. Core Scenario

1. 관리자가 후보지를 등록한다.
2. 후보지는 Google Maps 지도 위에 마커로 표시된다.
3. 사용자는 후보지를 클릭해 상세 패널을 연다.
4. 시스템은 후보지 반경 내 교통, 시설, 경쟁점, 유동인구 데이터를 조회한다.
5. 시스템은 항목별 점수와 총점을 계산한다.
6. 사용자는 후보지별 점수, 근거, 주변 데이터를 비교한다.

## 4. Tech Stack

### Frontend

- React
- Vite
- TanStack Query
- Zustand
- Google Maps JavaScript API
- Tailwind CSS
- Recharts

### Backend

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- Springdoc OpenAPI

### Database

- PostgreSQL
- PostGIS

### Infra

- Docker
- Docker Compose
- Nginx optional

## 5. Docker Compose Services

```text
postgres
- PostgreSQL with PostGIS extension
- stores users, locations, facilities, competitors, transit stops, scores

backend
- Spring Boot API server
- connects to postgres
- exposes REST API and Swagger UI

frontend
- React app
- connects to backend API

nginx optional
- reverse proxy for frontend and backend
```

## 6. Functional Requirements

### 6.1 Auth

- User can sign in with email and password.
- Backend returns JWT access token.
- Admin-only APIs require admin role.
- MVP can start with a seeded admin account.

### 6.2 Candidate Location Management

- User can create, read, update, and delete candidate locations.
- Required fields:
  - name
  - business type
  - address
  - latitude
  - longitude
- Optional fields:
  - rent price
  - memo
- User can register a location by clicking on the map.

### 6.3 Map View

- Display Google Maps.
- Display candidate locations as markers.
- Marker color depends on total score.
- Selected location shows:
  - name
  - address
  - business type
  - total score
  - last analyzed time
- User can toggle analysis radius:
  - 300m
  - 500m
  - 1000m

### 6.4 Nearby Data Analysis

For a selected location and radius, backend calculates:

- transit stop count
- subway station count
- bus stop count
- demand facility count
- competitor count
- average foot traffic
- rent score baseline

All distance-based queries should use PostGIS.

### 6.5 Location Scoring

The scoring model is rule-based and explainable.

```text
totalScore =
  footTrafficScore * 0.30
  + transportScore * 0.25
  + demandScore * 0.20
  + competitionScore * 0.15
  + rentScore * 0.10
```

Score range:

- each sub-score: 0 to 100
- total score: 0 to 100

Default weight:

| Metric | Weight | Direction |
| --- | ---: | --- |
| Foot traffic | 30% | higher is better |
| Transport accessibility | 25% | higher is better |
| Demand facilities | 20% | higher is better |
| Competition intensity | 15% | lower is better |
| Rent affordability | 10% | lower is better |

### 6.6 Report

Location detail page shows:

- total score
- score grade
- sub-score chart
- nearby data summary
- scoring explanation
- nearby facilities table
- nearby competitors table
- nearby transit stops table

Example explanation:

```text
500m radius contains 1 subway station and 8 bus stops, so transport accessibility is high.
Demand facility density is above average for this dataset.
Competitor count is moderate, so competition score is reduced.
```

### 6.7 CSV Upload

Admin can upload CSV files for:

- facilities
- competitors
- transit stops
- foot traffic samples

Upload behavior:

- validate required columns
- reject invalid latitude and longitude
- show row count summary
- store valid rows

## 7. Non-Functional Requirements

- Local environment must run with one Docker Compose command.
- Backend APIs must be documented with Swagger.
- README must include setup, screenshots, and architecture overview.
- Scoring logic must be covered by unit tests.
- API response shape should be consistent.
- Sensitive values such as Google Maps API key must be configured through environment variables.

## 8. Data Model

### users

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| email | varchar | unique |
| password | varchar | encoded |
| name | varchar |  |
| role | varchar | USER or ADMIN |
| created_at | timestamp |  |

### locations

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| name | varchar |  |
| business_type | varchar |  |
| address | varchar |  |
| latitude | decimal |  |
| longitude | decimal |  |
| geom | geography | PostGIS point |
| rent_price | integer | nullable |
| memo | text | nullable |
| created_at | timestamp |  |
| updated_at | timestamp |  |

### facilities

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| name | varchar |  |
| category | varchar | school, office, hospital, apartment, etc |
| address | varchar |  |
| latitude | decimal |  |
| longitude | decimal |  |
| geom | geography | PostGIS point |

### competitors

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| name | varchar |  |
| business_type | varchar |  |
| address | varchar |  |
| latitude | decimal |  |
| longitude | decimal |  |
| geom | geography | PostGIS point |

### transit_stops

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| name | varchar |  |
| type | varchar | SUBWAY or BUS |
| latitude | decimal |  |
| longitude | decimal |  |
| geom | geography | PostGIS point |

### foot_traffic_samples

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| base_date | date |  |
| hour | integer | 0 to 23 |
| latitude | decimal |  |
| longitude | decimal |  |
| geom | geography | PostGIS point |
| count | integer |  |

### location_scores

| Column | Type | Note |
| --- | --- | --- |
| id | bigint | primary key |
| location_id | bigint | foreign key |
| radius_meters | integer |  |
| foot_traffic_score | decimal |  |
| transport_score | decimal |  |
| demand_score | decimal |  |
| competition_score | decimal |  |
| rent_score | decimal |  |
| total_score | decimal |  |
| explanation | text |  |
| calculated_at | timestamp |  |

## 9. API Spec

### Auth

```text
POST /api/auth/login
POST /api/auth/signup
GET  /api/auth/me
```

### Locations

```text
GET    /api/locations
POST   /api/locations
GET    /api/locations/{locationId}
PUT    /api/locations/{locationId}
DELETE /api/locations/{locationId}
```

### Analysis

```text
POST /api/locations/{locationId}/analysis?radius=500
GET  /api/locations/{locationId}/score
GET  /api/locations/{locationId}/nearby?radius=500
```

### Admin Uploads

```text
POST /api/admin/uploads/facilities
POST /api/admin/uploads/competitors
POST /api/admin/uploads/transit-stops
POST /api/admin/uploads/foot-traffic
```

## 10. Frontend Pages

### /login

- email input
- password input
- login button
- error message

### /dashboard

- full-screen map
- candidate location list
- business type filter
- score range filter
- radius selector
- selected location detail panel

### /locations/new

- location form
- map click coordinate picker

### /locations/:id

- location summary
- total score
- score chart
- nearby data tables
- scoring explanation

### /admin/uploads

- CSV type selector
- file input
- upload result summary

## 11. MVP Scope

MVP must include:

- Docker Compose with postgres, backend, frontend
- Auth with seeded admin account
- Location CRUD
- Google Maps marker rendering
- PostGIS radius count query
- Score calculation API
- Score detail page
- CSV upload for at least one dataset
- README with setup and screenshots

Out of MVP:

- Kubernetes
- payment
- real-time updates
- production cloud deployment
- machine learning model training
- multi-tenant organization management

## 12. Portfolio README Points

README should emphasize:

- why this project was built for B2B GIS workflows
- architecture diagram
- local setup command
- Google Maps API key setup
- PostGIS usage
- score calculation formula
- screenshots or GIF
- sample API requests
- test command
- AI tool usage note
