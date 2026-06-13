# Demo Data

The demo bundle in `infra/demo-data/` uses small synthetic records around Gangnam-gu, Seoul. It is intended only for local portfolio demos and does not contain licensed or private GIS data.

## Files

- `locations.csv`: candidate business locations imported through `POST /api/admin/locations`
- `facilities.csv`: offices, schools, hospitals, and apartments imported through `POST /api/admin/uploads/facilities`
- `competitors.csv`: nearby competitor stores imported through `POST /api/admin/uploads/competitors`
- `transit-stops.csv`: subway and bus stops imported through `POST /api/admin/uploads/transit-stops`
- `foot-traffic.csv`: hourly sample counts imported through `POST /api/admin/uploads/foot-traffic`

## Import Flow

Start the local stack:

```powershell
docker compose up -d --build
```

Import the demo data:

```powershell
powershell -ExecutionPolicy Bypass -File infra/demo-data/import-demo-data.ps1
```

The script logs in with the seeded admin credentials from `.env.example`, creates the candidate locations, and uploads the four CSV datasets. The default target is `http://localhost:8080`.

To use custom credentials or a different backend URL:

```powershell
powershell -ExecutionPolicy Bypass -File infra/demo-data/import-demo-data.ps1 `
  -ApiBaseUrl "http://localhost:8080" `
  -AdminEmail "admin@example.com" `
  -AdminPassword "admin1234"
```

After import, open `http://localhost:5173/` and run analysis for a sample location with a 500m radius.
