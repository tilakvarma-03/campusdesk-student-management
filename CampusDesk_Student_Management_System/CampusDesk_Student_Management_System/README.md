# CampusDesk — Student Management System

A beginner-friendly B.Tech Advanced Programming Practice project using Java 17, Spring Boot 3, Spring Data JPA, MySQL, HTML, CSS and JavaScript.

## Features

- Add, view, search, update and delete student records (CRUD).
- Search by student ID, name, department or email.
- Filter by department.
- Dashboard statistics based on database records.
- Live database connection indicator.
- Input validation and duplicate ID/email checks.
- Responsive frontend served by the same Spring Boot application.

## Architecture

Browser (HTML/CSS/JavaScript) → REST API (Spring Boot Controller) → Spring Data JPA / Hibernate → MySQL.

## Requirements

- JDK 17 or newer
- Maven 3.6+ (or Maven support in VS Code)
- MySQL Server 8.x

## 1. Create the database

Open MySQL Workbench and run `database/schema.sql`.

Alternatively, the JDBC URL is configured to create `student_db` if it does not exist, provided the MySQL user has permission to create databases. The table is created/updated by JPA.

## 2. Configure database credentials

The default local settings are:
- Host: `localhost`
- Port: `3306`
- Database: `student_db`
- Username: `root`
- Password: `root`

If your MySQL root password is different, set environment variables before starting the app.

PowerShell example:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/student_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Do not commit real passwords or `.env` files to GitHub.

## 3. Run in VS Code

Open this folder in VS Code. Install:
- Extension Pack for Java
- Maven for Java (if not already included)

From the project root, run:

```powershell
mvn spring-boot:run
```

Open:

`http://localhost:8080`

## 4. Verify connectivity

Open:

`http://localhost:8080/api/health`

Expected response when MySQL is running:

```json
{
  "application": "UP",
  "database": "CONNECTED",
  "totalStudents": 0
}
```

The number may be greater than zero if you have already added records.

## 5. API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check application and database |
| GET | `/api/students` | List students |
| GET | `/api/students?search=tilak` | Search students |
| GET | `/api/students?department=CSE` | Filter by department |
| GET | `/api/students/{id}` | Get one student |
| GET | `/api/students/stats` | Dashboard counts |
| POST | `/api/students` | Add student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |

Example POST body:

```json
{
  "id": 101,
  "name": "Tilak",
  "department": "CSE",
  "email": "tilak@example.com"
}
```

## 6. Test using curl (PowerShell)

```powershell
curl.exe http://localhost:8080/api/health
curl.exe http://localhost:8080/api/students
```

Add a student:

```powershell
curl.exe -X POST http://localhost:8080/api/students `
  -H "Content-Type: application/json" `
  -d '{"id":101,"name":"Tilak","department":"CSE","email":"tilak@example.com"}'
```

## 7. Optional Docker run

If Docker Desktop is installed and running:

```powershell
docker compose up --build
```

Open `http://localhost:8080`. Docker Compose starts MySQL and the Java application together. The sample Docker database password is for local demonstration only; change it before any public deployment.

Stop:

```powershell
docker compose down
```

Keep database data:

```powershell
docker compose down
```

To delete the Docker database data as well (this permanently removes the demo database volume):

```powershell
docker compose down -v
```

## 8. GitHub

Create an empty GitHub repository, then from this folder:

```powershell
git init
git add .
git status
git commit -m "Build CampusDesk Student Management System"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/student-management-system.git
git push -u origin main
```

Check `git status` before committing. Never upload database passwords.

## 9. Deployment notes

GitHub stores the source code; it does not run the Spring Boot backend or MySQL database. Deploy the application and a persistent MySQL service to a hosting provider.

Set these environment variables in the hosting provider:

- `DB_URL`: JDBC URL using the provider's MySQL host, port and database.
- `DB_USERNAME`: database username.
- `DB_PASSWORD`: database password.
- `PORT`: normally supplied by the hosting provider.

The app serves its frontend and API from one origin, so no separate frontend hosting or CORS configuration is needed.

## Viva explanation

"The frontend is built with HTML, CSS and JavaScript. It sends HTTP requests to REST endpoints in the Spring Boot backend. The controller calls Spring Data JPA, which uses Hibernate to communicate with MySQL. The application supports Create, Read, Update and Delete operations. The health endpoint checks whether the database is actually reachable."

## Assessment mapping

- Database connectivity: `/api/health` checks the real database by running a repository count query.
- Database operations: add, list, search, update and delete records using MySQL.
- Viva: explain frontend → REST API → controller → repository/JPA → MySQL.
