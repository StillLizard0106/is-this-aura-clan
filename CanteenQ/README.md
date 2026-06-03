CanteenQ

Status: Completed — Presentation complete. Cloud deployment planned as a future update.

Backend setup (Java Spring Boot)

- Default runtime: PostgreSQL persistence is enabled by default.
- To force the Postgres profile explicitly, run:
  ./backend/gradlew bootRun -Dspring.profiles.active=postgres

- Required env vars (used by application-postgres.properties):
  - POSTGRES_URL (default: jdbc:postgresql://localhost:5432/canteenq)
  - POSTGRES_USERNAME
  - POSTGRES_PASSWORD

- Firebase Admin configuration (optional but recommended for auth verification):
  Set in application properties or environment:
  - firebase.admin.enabled=true
  - firebase.admin.credentials-path=/absolute/path/to/service-account.json
  OR
  - firebase.admin.credentials-json="<raw JSON>" (or base64 encoded JSON)

  Optional: restrict allowed student email domain:
  - firebase.admin.allowed-email-domain=students.nu-laguna.edu.ph

  Fallbacks supported:
  - GOOGLE_APPLICATION_CREDENTIALS env var
  - Application Default Credentials (when running on GCP)

- Notes:
  - The backend supports running without Firebase Admin configured; in that case auth endpoints will return a clear error.
  - The backend now defaults to PostgreSQL so data persists across restarts.
  - Automated tests still run against H2 in `backend/src/test/resources/application.properties`.

Current implemented order rules:

- Pickup times must be at least 15 minutes ahead.
- Pickup times must be within 1 week of the current time.
- Pickup times must be between 7:00 AM and 6:00 PM.
- Each stall has a maximum active queue of 100 orders.
- The student UI shows queue usage and remaining slots.

---

Implemented features (summary):

- Staff dashboard: queue overview (5 most recent orders), revenue summary, and single-stall access.
- Menu management for staff: full CRUD for menu items scoped to assigned stall.
- Smooth in-page navigation: "Manage My Stall Menu" and "Back to Staff Dashboard" smooth scrolling.
- PostgreSQL persistence enabled and verified — data persists across backend restarts.

For details, see the staff pages in `frontend/src/pages/staff/` and the backend staff endpoints under `/api/staff/`.

---

**Firebase Admin — Local enable & verification**

Short checklist to enable Firebase Admin locally and verify end-to-end:

1. Obtain a Firebase Service Account JSON from the Firebase Console (Project Settings → Service accounts → Generate new private key).
2. Place the JSON file somewhere on your machine (example: `C:\secrets\firebase-service-account.json`).
3. Configure the backend to enable Firebase Admin. You can either edit `backend/src/main/resources/application-postgres.properties` or pass system properties when starting the app.

PowerShell example (start backend with Postgres profile and Firebase enabled):

```powershell
cd backend
.\gradlew bootRun -Dspring.profiles.active=postgres -Dfirebase.admin.enabled=true -Dfirebase.admin.credentials-path="C:\secrets\firebase-service-account.json" -Dfirebase.admin.allowed-email-domain=students.nu-laguna.edu.ph
```

Or set environment variables (example):

```powershell
$env:POSTGRES_URL = "jdbc:postgresql://localhost:5432/canteenq"
$env:POSTGRES_USERNAME = "postgres"
$env:POSTGRES_PASSWORD = "yourpassword"
$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\secrets\firebase-service-account.json"
./gradlew bootRun -Dspring.profiles.active=postgres -Dfirebase.admin.enabled=true -Dfirebase.admin.allowed-email-domain=students.nu-laguna.edu.ph
```

Quick verification via HTTP (replace host/port if different):

- With a real Firebase ID token obtained from the client, call:

```bash
curl -i -H "Authorization: Bearer <REAL_FIREBASE_ID_TOKEN>" http://localhost:8080/api/auth/verify
```

- For local testing with the dev/mock token format (works when `firebase.admin.enabled=false` or for dev fallback):

```bash
# Valid (allowed domain)
curl -i -H "Authorization: Bearer mock-jwt-token:demo-uid:student@students.nu-laguna.edu.ph" http://localhost:8080/api/auth/verify

# Invalid domain (should return a clear error when allowed-email-domain is set)
curl -i -H "Authorization: Bearer mock-jwt-token:demo-uid:bad@other.com" http://localhost:8080/api/auth/verify
```

What to expect:
- Successful verification returns HTTP 200 with a JSON body including the synced user role.
- If `firebase.admin.allowed-email-domain` is configured and the token email doesn't match, the backend responds with a 400 and a message indicating the domain is not allowed.
- If Firebase Admin is enabled but credentials are missing/invalid, backend returns a 503 with a `FIREBASE_AUTH_NOT_CONFIGURED` message.

If you want, I can also add a small troubleshooting section or implement a UI message that explains the domain restriction on the Register/Login pages.

**Troubleshooting**

- **Domain restriction policy:** The backend enforces `firebase.admin.allowed-email-domain` whenever the property is configured, including with the mock dev token flow. If that property is set, non-matching email domains should be rejected even in local dev mode.
- **Firebase Admin enabled but startup fails:** Ensure the service account JSON path is correct and readable. Example env var: `GOOGLE_APPLICATION_CREDENTIALS=C:\secrets\firebase-service-account.json` or pass `-Dfirebase.admin.credentials-path="C:\secrets\firebase-service-account.json"` when starting the app.
- **Quick local test (mock token):** Use the mock dev token format to verify domain rejection without real Firebase Admin credentials:

```bash
curl -i -H "Authorization: Bearer mock-jwt-token:demo-uid:student@your.school.edu" http://localhost:8080/api/auth/verify
```

- **When you want me to run the real verification:** Reply with the full path to your Firebase service account JSON (or set `GOOGLE_APPLICATION_CREDENTIALS`), and I'll restart the backend with Firebase Admin enabled and run the real-token test for you.

