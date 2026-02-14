# VisualAlgo

Algorithm visualization starter app built with:
- Java 25
- Spring Boot 4
- PostgreSQL
- React (Vite)

## Current Features
- Scenario CRUD API (`/api/scenarios`)
- Pathfinding run API (`/api/runs`)
- Supported algorithms:
  - BFS
  - DFS
  - DIJKSTRA
- React grid visualizer with:
  - Wall editing
  - Start/target placement
  - Animation controls (play/pause/step/speed)
  - Algorithm selector

## Project Structure
- Backend: `/Users/junzhou/IdeaProjects/VisualAlgo/backend`
- Frontend: `/Users/junzhou/IdeaProjects/VisualAlgo/frontend`
- SQL scripts: `/Users/junzhou/IdeaProjects/VisualAlgo/sql`

## Prerequisites
- JDK 25
- Docker Desktop
- Node.js 18+ and npm

## One-Command Docker Run (Full App)
From `/Users/junzhou/IdeaProjects/VisualAlgo`:

```bash
docker compose up -d --build
```

App URLs:
- Frontend: `http://localhost:5173`
- Backend API (via frontend proxy): `http://localhost:5173/api/...`
- Direct Postgres: `localhost:5432`

Stop stack:

```bash
docker compose down
```

## 1) Start PostgreSQL (Docker)
From `/Users/junzhou/IdeaProjects/VisualAlgo`:

```bash
docker compose up -d postgres
docker compose ps
```

PostgreSQL config:
- Host: `localhost`
- Port: `5432`
- DB: `mydatabase`
- User: `myuser`
- Password: `secret`

Notes:
- `compose.yaml` mounts `./sql` into `/docker-entrypoint-initdb.d`.
- `sql/algos.sql` initializes `scenario`, `algorithm_run`, and `algorithm_step`.

## 2) Run Backend
From `/Users/junzhou/IdeaProjects/VisualAlgo/backend`:

```bash
./mvnw spring-boot:run
```

Backend URL:
- `http://localhost:8080`

Run tests:

```bash
./mvnw test
```

## 3) Run Frontend
From `/Users/junzhou/IdeaProjects/VisualAlgo/frontend`:

```bash
npm install
npm run dev
```

Frontend URL:
- `http://localhost:5173`

Vite proxy forwards `/api/*` to backend `http://localhost:8080`.

## API Reference

### Create Scenario
`POST /api/scenarios`

```json
{
  "name": "Demo Grid",
  "type": "GRID",
  "dataJson": "{\"rows\":14,\"cols\":28,\"walls\":[]}"
}
```

### List Scenarios
`GET /api/scenarios`

### Get Scenario by ID
`GET /api/scenarios/{id}`

### Run Algorithm
`POST /api/runs`

```json
{
  "algorithm": "BFS",
  "rows": 14,
  "cols": 28,
  "start": { "row": 2, "col": 2 },
  "target": { "row": 11, "col": 24 },
  "walls": [
    { "row": 5, "col": 5 },
    { "row": 5, "col": 6 }
  ]
}
```

`algorithm` accepted values:
- `BFS`
- `DFS`
- `DIJKSTRA`

## Troubleshooting

### Connection refused on `localhost:5432`
- Ensure Docker Desktop is running.
- Start DB container:

```bash
cd /Users/junzhou/IdeaProjects/VisualAlgo
docker compose up -d postgres
docker compose ps
```

### Frontend cannot call backend
- Confirm backend is running on `:8080`.
- Confirm frontend runs via Vite (`npm run dev`) so proxy is active.

### Reinitialize database
If you need a clean DB:

```bash
cd /Users/junzhou/IdeaProjects/VisualAlgo
docker compose down -v
docker compose up -d postgres
```

## Next Improvements
- Add A* algorithm and shortest-path reconstruction
- Stream steps over WebSocket instead of returning all at once
- Persist run/step data from API service into SQL tables
