# Talent Matcher LLM

End-to-end talent acquisition assistant running locally in Docker. Upload resumes, compute OpenAI embeddings, match candidates to job descriptions, and explore results with analytics and chat-style explanations.

## Prerequisites
- Windows 11 with Docker Desktop
- OpenAI API key available as environment variable `OPENAI_API_KEY`

## Quick start
1. Clone the repository and copy `.env.example` to `.env`, filling in `OPENAI_API_KEY`, database credentials, and a strong `JWT_SECRET` (32+ characters).
2. Run `start.bat` from a Windows terminal. This builds backend and frontend images and launches docker compose with PostgreSQL.
3. Open the frontend at http://localhost:5173 and log in with default credentials:
   - Username: `admin`
   - Password: `admin`

## Services
- Backend: Spring Boot (Java 21) at http://localhost:8080
- Frontend: React + TypeScript (MUI) at http://localhost:5173
- Database: PostgreSQL 16 container for users and resume metadata
- Storage: resume files saved under the `storage/resumes` volume

## Environment variables
- `OPENAI_API_KEY`: required for embeddings and explanations.
- `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`: database settings (default `talent` / `talentpassword` / `talentdb`).
- `JWT_SECRET`: secret string for signing JWTs (32+ characters recommended).
- `VITE_API_URL`: optional override for frontend API base (defaults to `http://localhost:8080/api`).

## Build manually
```
# Backend
cd backend
mvn package -DskipTests

# Frontend
cd ../frontend
npm install
npm run build
```

## Authentication
Users are stored in PostgreSQL. On first start a default admin user is created with password `admin` and is marked to require password change; use the `/api/auth/change-password` endpoint (authenticated) to update.
