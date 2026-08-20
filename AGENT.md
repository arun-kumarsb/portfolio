# AI Coding Assistant & Agent Guidelines (`AGENT.md`)

Welcome, Agent. This document defines the operational philosophy, behavior guidelines, architectural conventions, and strict project constraints when working on **Arun's Portfolio & Engineering Ecosystem**.

---

## 1. Core Principles & Philosophy

1. **Maintain Truthful & Authentic Representation**:
   - Arun is a dedicated **engineering student (B.E. CSE 2023–2027)** and backend software developer.
   - His ambition in defense technology and armed forces officer commissioning (CDS/AFCAT) is an **aspirational personal goal**, influenced by his father's 30 years of service in the SIKH LI.
   - **Never exaggerate claims**. Do not present Arun as a defense engineer, military officer, or someone with operational classified military experience.
   - **Forbidden Word**: Never use the word *"discipline"* or *"military discipline"* to describe his personality or qualification. Use *service, responsibility, purpose, engineering, personal inspiration, contribution*.
   - **STCS Simulation**: The *Secure Tactical Communication & Command System (STCS)* is an academic simulation project. Always maintain the clear disclaimer: *"Academic simulation — not connected to real military systems or operational infrastructure."*

2. **Scope Separation**:
   - **Main Portfolio (`index.html`)**: Focuses on core software engineering, Java, Spring Boot, MySQL, REST APIs, distributed systems, and database architecture.
   - **Defense Ambition Page (`defense-ambition.html`)**: Dedicated route exploring his personal background, service aspirations (CDS/AFCAT), and technical learning areas (distributed systems, communication protocols, AI/ML concepts, and cybersecurity fundamentals).

3. **Production-Grade Engineering**:
   - Write clean, modular, maintainable, and self-documenting code.
   - Strictly adhere to layered architecture in backend: `Controller -> Service -> Repository -> Entity/DTO`.
   - Maintain resilient fallback mechanisms in both frontend (`api.js` mock data fallback) and backend (`DataSourceConfig.java` H2 in-memory fallback).

---

## 2. Technology Stack & Directory Structure

```
portfolio/
├── backend/                             # Java Spring Boot 3 Backend Monolith
│   ├── src/main/java/com/arun/portfolio/
│   │   ├── config/                      # SecurityConfig, CorsConfig, DataSourceConfig, DataInitializer
│   │   ├── controller/                  # AuthController, ProjectController, SkillController, etc.
│   │   ├── dto/                         # Request / Response Transfer Objects
│   │   ├── entity/                      # JPA Entities (User, Project, Skill, Education, Contact)
│   │   ├── repository/                  # Spring Data JPA Interfaces
│   │   ├── security/                    # JwtAuthFilter, JwtTokenProvider, CustomUserDetailsService
│   │   └── service/                     # Business Logic Interfaces & Implementations
│   └── pom.xml                          # Maven dependencies (Spring Boot 3.3.4, Java 17, JJWT 0.12.6)
├── frontend/                            # Vanilla ES6 + Modern Responsive CSS
│   ├── css/
│   │   ├── style.css                    # Design System tokens, typography, dark mode components
│   │   └── responsive.css               # Mobile-first swipe carousels, Android 360px-412px rules
│   ├── js/
│   │   ├── api.js                       # Centralized HTTP layer with 30s timeout & mock fallback
│   │   ├── main.js                      # DOM events, navigation spy, modal bindings
│   │   ├── projects.js                  # Dynamic project card rendering & filter logic
│   │   ├── skills.js                    # Skills grid renderer & category switching
│   │   ├── education.js                 # Education timeline renderer
│   │   ├── contact.js                   # Form validation, rate limiting, and live status badge
│   │   └── admin.js                     # JWT Admin CMS dashboard controller & resume manager
│   ├── index.html                       # Main single-page portfolio & digital resume modal
│   ├── defense-ambition.html            # Dedicated Defense & Service Ambition deep-dive page
│   └── admin.html                       # Secured JWT Admin CMS dashboard
├── Dockerfile                           # Multi-stage Maven + OpenJDK 17 build for Render
├── .env.example                         # Template for environment variables
└── README.md                            # Comprehensive project overview
```

---

## 3. Operational Rules & Workflows

### 3.1 Git & Deployment Etiquette
- Whenever creating or modifying frontend or backend files, ensure cross-platform newline compatibility (`LF`/`CRLF`).
- Always run `git status` to verify modified and untracked files.
- Commit with clear, descriptive conventional commit messages (e.g. `feat: ...`, `fix: ...`, `docs: ...`, `refactor: ...`).
- Push to `origin main` to trigger automatic CI/CD builds on **Vercel** (frontend) and **Render** (backend Docker).

### 3.2 Backend Code Standards
- Java 17 records or standard classes with Lombok / explicit getters and setters.
- Strict DTO request validation using `jakarta.validation.constraints.*` (`@NotBlank`, `@Email`, `@Size`).
- Centralized exception handling via `@RestControllerAdvice` returning standard `ApiResponse<T>` wrappers.
- Never commit hardcoded secrets, JWT keys, or database credentials.

### 3.3 Frontend Code Standards
- Zero external heavyweight dependencies (no jQuery, no Bootstrap); use clean Vanilla ES6+ and modern CSS.
- Ensure all clickable cards, buttons, and inputs have `min-height: 44px` on mobile for optimal touch ergonomics.
- Ensure horizontal scroll containers (`.skills-filter-container`, `.admin-nav-tabs`) have `flex-wrap: nowrap; overflow-x: auto; -webkit-overflow-scrolling: touch;`.
- Avoid hardcoded fake `TODO:` strings; use real profile links (`github.com/arun-kumarsb`, `linkedin.com/in/arunksb`) or clean semantic placeholders.
