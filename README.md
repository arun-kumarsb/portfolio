# Arun's Full-Stack Developer Portfolio & Admin CMS

A modern, professional, responsive full-stack developer portfolio and content management system showcasing engineering projects, technical skills, academic background, interactive contact messaging, and a **Spring Security + JWT protected Admin Control Panel**. Built with a clean **Vanilla HTML/CSS/JavaScript** frontend and a robust **Java + Spring Boot 3 REST API** backend with **MySQL & Spring Data JPA** persistence.

---

## 1. Architecture Overview

This project adheres strictly to layered architecture and separation of concerns:

```text
Visitor / Admin Browser (Vanilla HTML5 / CSS3 / JavaScript)
    ↓
REST API (JSON over HTTP / Fetch API via js/api.js with JWT Bearer Token)
    ↓
Spring Security (Stateless JWT Filter & Role-Based Authorization)
    ↓
Spring Boot Controller Layer (@RestController, DTO boundary, Jakarta Validation)
    ↓
Service Layer (@Service, Business Logic & Transactions)
    ↓
Repository Layer (Spring Data JPA / Hibernate)
    ↓
MySQL Database (Relational persistence)
```

### Key Architectural Highlights
- **No Heavy Frontend Frameworks**: Uses semantic HTML5, modern CSS3 variables for clean theming, and modular vanilla JavaScript.
- **Spring Security & JWT Authentication**: Secured endpoints for administrative CRUD operations with stateless token-based authorization and BCrypt password encryption.
- **Admin Control Panel**: An administrative dashboard (`frontend/admin.html`) to manage all portfolio data (Projects, Skills, Education, and Inbox messages) without touching code.
- **Strict DTO Boundaries**: Database entities are decoupled from API contracts using dedicated request/response DTOs.
- **Centralized Exception Handling**: `@RestControllerAdvice` translates internal exceptions and validation constraints into clean JSON error objects with zero leaked stack traces.

---

## 2. Tech Stack

| Domain | Technologies |
| :--- | :--- |
| **Frontend** | HTML5, CSS3 (Dark Developer Aesthetic), Vanilla JavaScript (ES6 Modules/IIFE) |
| **Backend** | Java 17+, Spring Boot 3.3.x, Spring Security 6, JJWT (0.12.x), Spring Data JPA, Hibernate, Jakarta Validation |
| **Database** | MySQL 8.0+ (with resilient fallback to local relational storage) |
| **Build & Tooling** | Maven Wrapper (`mvnw.cmd` / `mvnw`), Git |

---

## 3. Project Structure

```text
portfolio/
├── README.md
├── .gitignore
├── frontend/
│   ├── index.html              # Public single-page portfolio layout
│   ├── admin.html              # Secure Admin CMS control panel
│   ├── css/
│   │   ├── style.css           # Core dark developer aesthetic & styles
│   │   └── responsive.css      # Responsive media queries (Desktop/Tablet/Mobile)
│   ├── js/
│   │   ├── api.js              # REST API client with JWT token management
│   │   ├── admin.js            # Admin authentication & interactive CRUD logic
│   │   ├── skills.js           # Public skills dynamic loader & category filtering
│   │   ├── projects.js         # Public project cards dynamic rendering
│   │   ├── education.js        # Public education timeline rendering
│   │   ├── contact.js          # Public form validation & submission handler
│   │   └── main.js             # Navigation, scroll-spy, mobile drawer
│   └── assets/
│       ├── images/
│       └── icons/
└── backend/
    ├── pom.xml
    ├── mvnw.cmd / mvnw
    └── src/
        ├── main/
        │   ├── java/com/arun/portfolio/
        │   │   ├── PortfolioApplication.java
        │   │   ├── config/            # CorsConfig, DataInitializer, DataSourceConfig
        │   │   ├── security/          # SecurityConfig, JwtUtils, JwtAuthFilter
        │   │   ├── controller/        # AuthController, ProjectController, SkillController, EducationController, ContactController
        │   │   ├── dto/               # AuthRequest, AuthResponse, ProjectRequest/Response, SkillRequest/Response, EducationRequest/Response, ContactRequest/Response
        │   │   ├── entity/            # Project, Skill, Education, ContactMessage
        │   │   ├── exception/         # GlobalExceptionHandler, ResourceNotFoundException, ErrorResponse
        │   │   ├── repository/        # Spring Data JPA Repositories
        │   │   └── service/           # AuthService, ProjectService, SkillService, EducationService, ContactService
        │   └── resources/
        │       └── application.properties
        └── test/
            ├── java/com/arun/portfolio/
            │   ├── PortfolioApplicationTests.java
            │   ├── ContactControllerTest.java
            │   └── AdminSecurityControllerTest.java
            └── resources/
                └── application.properties
```

---

## 4. Admin Credentials & Configuration

By default, the initial admin credentials are:
* **Username**: `admin`
* **Password**: `adminpassword123`

You can customize these via environment variables or in `application.properties`:
```bash
# Windows PowerShell
$env:APP_ADMIN_USERNAME="arun_admin"
$env:APP_ADMIN_PASSWORD="my_custom_secure_password"
$env:APP_JWT_SECRET="YourCustomVeryLongSecretKeyForJwtSigningAtLeast256BitsLong2026!"
```

---

## 5. REST API Endpoints

### Public Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Admin authentication $\rightarrow$ returns JWT token |
| `GET` | `/api/projects` | Fetch all portfolio projects |
| `GET` | `/api/projects/{id}` | Fetch a single project by ID |
| `GET` | `/api/skills` | Fetch all technical skills (optional `?category=`) |
| `GET` | `/api/education` | Fetch academic background & history |
| `POST` | `/api/contact` | Submit a new contact message |

### Protected Admin Endpoints (`ROLE_ADMIN` with `Authorization: Bearer <token>`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/auth/me` | Verify authenticated admin identity |
| `POST` | `/api/projects` | Create a new project |
| `PUT` | `/api/projects/{id}` | Update an existing project |
| `DELETE` | `/api/projects/{id}` | Delete a project |
| `POST` | `/api/skills` | Create a new skill |
| `PUT` | `/api/skills/{id}` | Update a skill |
| `DELETE` | `/api/skills/{id}` | Delete a skill |
| `POST` | `/api/education` | Create an education entry |
| `PUT` | `/api/education/{id}`| Update an education entry |
| `DELETE` | `/api/education/{id}`| Delete an education entry |
| `GET` | `/api/contact` | Retrieve all contact messages in inbox |
| `DELETE` | `/api/contact/{id}` | Delete a contact message |

---

## 6. How to Run the Application

### 1. Start the Spring Boot Backend
Navigate to the `backend` folder and run the Maven wrapper:

```bash
cd backend

# On Windows:
.\mvnw.cmd spring-boot:run

# On Linux / macOS:
./mvnw spring-boot:run
```

The Spring Boot backend will start on **`http://localhost:8080`**.

### 2. Launch the Public Portfolio or Admin Dashboard

```bash
# Public Portfolio:
Start-Process "C:\Users\iyurs\portfolio\frontend\index.html"

# Admin Dashboard:
Start-Process "C:\Users\iyurs\portfolio\frontend\admin.html"

# Or serve via local web server:
python -m http.server 3000 --directory C:\Users\iyurs\portfolio\frontend
```

Open `http://localhost:3000/admin.html` and sign in with `admin` / `adminpassword123` to manage projects, skills, education, and view recruiter messages.

### 3. Run Automated Tests
```bash
cd backend
.\mvnw.cmd test
```
All unit and integration tests (public APIs, validations, security auth filters, and admin endpoints) pass with 0 failures.
