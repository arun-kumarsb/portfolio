# Technical Requirements Document (`TRD.md`)

## 1. System Architecture Diagram

```
+-----------------------------------------------------------------------------------+
|                                  CLIENT LAYER                                     |
|                                                                                   |
|   +--------------------------+  +--------------------------+  +-----------------+ |
|   |   Main Portfolio         |  |   Defense Ambition Route |  |   Admin CMS     | |
|   |   (frontend/index.html)  |  |   (defense-ambition.html)|  |   (admin.html)  | |
|   +-------------+------------+  +-------------+------------+  +--------+--------+ |
|                 |                             |                        |          |
|                 +-----------------------------+------------------------+          |
|                                               |                                   |
|                                 (Vanilla ES6 Fetch / REST)                        |
+-----------------------------------------------|-----------------------------------+
                                                v
+-----------------------------------------------------------------------------------+
|                        SPRING BOOT 3 REST API BACKEND                             |
|                               (Render Docker Node)                                |
|                                                                                   |
|   [CorsFilter] -> [JwtAuthFilter] -> [Spring Security (FilterChain)]              |
|                                                                                   |
|   +---------------------------------------------------------------------------+   |
|   |                           REST CONTROLLERS                                |   |
|   |  AuthController | ProjectController | SkillController | ContactController |   |
|   +-------------------------------------+-------------------------------------+   |
|                                         |                                         |
|                                         v                                         |
|   +---------------------------------------------------------------------------+   |
|   |                            SERVICE LAYER                                  |   |
|   |  ProjectService | SkillService | EducationService | ContactService | Auth |   |
|   +-------------------------------------+-------------------------------------+   |
|                                         |                                         |
|                                         v                                         |
|   +---------------------------------------------------------------------------+   |
|   |                        DATA ACCESS / JPA REPOSITORIES                     |   |
|   |    ProjectRepository | SkillRepository | EducationRepository | Message    |   |
|   +-------------------------------------+-------------------------------------+   |
+-----------------------------------------|-----------------------------------------+
                                          |
                        (HikariCP Pool / SSL Connection)
                                          |
                                          v
+-----------------------------------------------------------------------------------+
|                             PERSISTENCE TIER                                      |
|                                                                                   |
|   Primary: Aiven Cloud MySQL 8.x Cluster (SSL Enabled)                            |
|   Fallback: In-Memory H2 DB (jdbc:h2:mem:portfoliodb) if Cloud unreachable       |
+-----------------------------------------------------------------------------------+
```

---

## 2. Technology Stack & Specifications

### 2.1 Backend (Spring Boot 3.3.4)
- **Language / Runtime**: Java 17 (OpenJDK 17).
- **Core Framework**: Spring Boot 3.3.4 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`).
- **Security & JWT**: Spring Security 6 + JJWT 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
- **Database Driver**: `mysql-connector-j` (MySQL 8.x) + `com.h2database:h2` for resilient in-memory fallback.
- **Connection Pool**: HikariCP with connection timeout tests and multi-format URI parsing.

### 2.2 Frontend (Vanilla ES6 Web Stack)
- **Markup**: Semantic HTML5 with accessibility attributes (`aria-*`), `viewport-fit=cover`.
- **Styling**: CSS Custom Properties design system, modular layout with flexbox and CSS grid, `responsive.css` touch scroll carousels.
- **JavaScript**: Modular Vanilla ES6 (`api.js`, `main.js`, `projects.js`, `skills.js`, `education.js`, `contact.js`, `admin.js`).
- **State & Storage**: JWT stored in `localStorage`, dynamic mock data fallback in `api.js`.

---

## 3. Database Schema & Relational Models

```sql
-- Projects Table
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    technologies VARCHAR(255) NOT NULL,
    github_url VARCHAR(500),
    live_url VARCHAR(500),
    image_url VARCHAR(500),
    featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Skills Table
CREATE TABLE skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    proficiency VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Education Table
CREATE TABLE education (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(255) NOT NULL,
    field VARCHAR(255) NOT NULL,
    start_date VARCHAR(50) NOT NULL,
    end_date VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Contact Messages Table
CREATE TABLE contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 4. REST API Endpoint Specifications

| Method | Endpoint | Access | Request Body | Response Format |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | `{ username, password }` | `{ success, data: { token, username, role } }` |
| `GET` | `/api/auth/me` | Authenticated | None | `{ success, data: { username, role } }` |
| `GET` | `/api/projects` | Public | None | `{ success, data: [ ProjectDTO ] }` |
| `POST` | `/api/projects` | Authenticated | `ProjectDTO` | `{ success, data: ProjectDTO }` |
| `PUT` | `/api/projects/{id}` | Authenticated | `ProjectDTO` | `{ success, data: ProjectDTO }` |
| `DELETE`| `/api/projects/{id}` | Authenticated | None | `{ success, message: "Project deleted" }` |
| `GET` | `/api/skills` | Public | None | `{ success, data: [ SkillDTO ] }` |
| `POST` | `/api/skills` | Authenticated | `SkillDTO` | `{ success, data: SkillDTO }` |
| `DELETE`| `/api/skills/{id}` | Authenticated | None | `{ success, message: "Skill deleted" }` |
| `GET` | `/api/education` | Public | None | `{ success, data: [ EducationDTO ] }` |
| `POST` | `/api/education` | Authenticated | `EducationDTO` | `{ success, data: EducationDTO }` |
| `DELETE`| `/api/education/{id}` | Authenticated | None | `{ success, message: "Education deleted" }` |
| `POST` | `/api/contact` | Public | `{ name, email, message }` | `{ success, message: "Message sent" }` |
| `GET` | `/api/contact` | Authenticated | None | `{ success, data: [ ContactMessageDTO ] }` |
| `DELETE`| `/api/contact/{id}` | Authenticated | None | `{ success, message: "Message deleted" }` |
| `GET` | `/api/health` | Public | None | `{ status: "UP", timestamp: "..." }` |

---

## 5. Deployment & CI/CD Pipeline

### 5.1 Render Backend Deployment (`Dockerfile`)
Multi-stage Docker build ensuring minimal production image size:
```dockerfile
# Stage 1: Build JAR with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal OpenJDK Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
```

### 5.2 Vercel Frontend Deployment
- Root Directory configured to `frontend/`.
- Automatic edge deployment on `git push origin main`.
- Single-page application routing rules in `vercel.json`.
