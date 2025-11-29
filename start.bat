@echo off
setlocal
if not defined OPENAI_API_KEY (
  echo Please set OPENAI_API_KEY in your environment or .env file.
)

echo Building backend...
cd backend
call mvn -B -ntp package -DskipTests
cd ..

echo Building containers...
docker compose build

echo Starting stack...
docker compose up -d

echo Application available:
echo Backend: http://localhost:8080

echo Frontend: http://localhost:5173
