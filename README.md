# GIS B2B Location Analysis Platform

GIS 기반 B2B 입지 분석 플랫폼입니다. 후보 매장 위치를 지도에 등록하고 교통 접근성, 주변 수요시설, 경쟁 강도, 유동인구, 임대료를 기준으로 입지 점수를 계산해 비교 리포트를 제공합니다.

## Structure

- `backend/`: Spring Boot API, PostGIS 연동, 인증, 공간 분석, 스코어링
- `frontend/`: React 관리 화면, Google Maps 지도, 분석 리포트 UI
- `infra/`: Docker Compose, 로컬 실행 환경, 배포/운영 설정
- `docs/`: 요구사항, 태스크, 포트폴리오 문서

## Local Runtime

Copy the local environment template and start the Docker Compose stack:

```sh
cp .env.example .env
docker compose up -d
```

The base stack starts:

- `postgres`: PostgreSQL/PostGIS on `localhost:5432`
- `backend`: placeholder service with Spring Boot environment variables
- `frontend`: placeholder service with Vite environment variables

Backend and frontend placeholders are replaced by real application containers in their implementation tasks.

## Tasks

- [Task index](docs/tasks/README.md)
- [Backend tasks](docs/tasks/backend/)
- [Frontend tasks](docs/tasks/frontend/)
- [Infra tasks](docs/tasks/infra/)
