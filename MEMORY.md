# Project Memory & Knowledge Base (`MEMORY.md`)

## 1. Developer Profile & Context
- **Name**: Arun
- **Role**: Engineering Student (B.E. Computer Science & Engineering, 2023–2027) & Software Developer.
- **Core Strengths**: Backend Engineering, Java 17, Spring Boot 3.x, RESTful API Design, Spring Data JPA / Hibernate, Relational Database Modeling (MySQL), Distributed Systems Concepts, Clean Architecture.
- **GitHub**: `https://github.com/arun-kumarsb` (Repo: `https://github.com/arun-kumarsb/portfolio.git`)
- **LinkedIn**: `https://linkedin.com/in/arunksb/`

---

## 2. Personal Heritage & Defense Ambition
- **Family Lineage**: Proud Army Brat; father served 30 dedicated years in the prestigious **SIKH LI (Sikh Light Infantry)** of the Indian Army.
- **Two Distinct Future Pathways**:
  1. **Service Ambition**: Exploring the possibility of becoming an Army Officer through competitive entry routes (such as **CDS or AFCAT**), subject to eligibility, examination, selection, and medical requirements. (Strictly framed as an aspiration and personal goal, never claiming current military experience or guaranteed outcome).
  2. **Technology Ambition**: Building rigorous engineering skills in software engineering, backend systems, distributed architectures, and communication systems to potentially contribute to defense technology, research organizations (**DRDO, HAL, IISc Defense Projects**), and national security challenges.
- **Tone & Guardrails**:
  - **No exaggerated claims**: Never claim to already be a defense engineer, military officer, or to have built real-world classified/military infrastructure.
  - **Forbidden vocabulary**: Never describe personality using *"discipline"*, *"military discipline"*, *"warrior mindset"*, etc.
  - **STCS Simulation**: The *Secure Tactical Communication & Command System (STCS)* is strictly an **academic system design simulation** exploring backend architecture, event-driven messaging, and telemetry propagation.
  - **Content Scope**: AI/ML and Cybersecurity are retained strictly on `defense-ambition.html` as academic learning interests; the main portfolio (`index.html`) focuses purely on software engineering, backend systems, distributed architectures, and database design.

---

## 3. Infrastructure & Deployment Topology

| Layer | Platform / Host | Endpoint / Connection | Details |
| :--- | :--- | :--- | :--- |
| **Frontend** | **Vercel** | `https://<vercel-domain>.vercel.app` | Static Vanilla ES6, CSS Design System, Responsive Mobile Touch |
| **Backend API**| **Render** | `https://portfolio-lk3x.onrender.com/api` | Multi-stage Docker build, Spring Boot 3.3.4, Java 17 |
| **Database** | **Aiven Cloud** | `portfoliodb-iyur-e62b.b.aivencloud.com:11674/defaultdb` | MySQL 8.x Cloud Cluster (SSL Required) |
| **Fallback DB**| **Local / In-Memory** | `jdbc:h2:mem:portfoliodb` | Resilient automatic failover if cloud DB is unreachable |

---

## 4. Key Architectural Decisions & Solutions

### 4.1 Resilient Multi-Format DataSource (`DataSourceConfig.java`)
- **Problem**: Cloud providers (like Render or Aiven) sometimes inject MySQL connection URIs in raw `mysql://user:pass@host:port/db?ssl-mode=REQUIRED` format rather than standard JDBC `jdbc:mysql://...` format, causing Spring Boot HikariCP driver failure.
- **Solution**: Implemented smart URI parser in `DataSourceConfig.java` that automatically checks for `mysql://`, extracts username, password, host, and port, converts SSL flags to `sslmode=require&useSSL=true&allowPublicKeyRetrieval=true`, and tests the live connection. If the cloud database is unavailable, it seamlessly boots an in-memory H2 database with zero application crashes.

### 4.2 Render Cold-Start & Health Check Handling (`api.js`)
- **Problem**: Free tier instances on Render spin down after inactivity, taking up to 25–30 seconds to wake up. An 8s timeout was causing the frontend to prematurely assume the backend was offline.
- **Solution**: Increased `TIMEOUT_MS` to 30,000ms (30s) in `frontend/js/api.js`, added retry mechanisms, and implemented dynamic status badges (`REST API Connected (Live Cloud)` vs `REST API Connected (Port 8080)` vs `Static Preview`).

### 4.3 Mobile & Android Responsiveness Overhaul (`responsive.css`)
- **Problem**: Skill filter buttons and Admin dashboard tabs wrapped into multiple cramped rows or caused horizontal window overflow on Android devices (360px–412px viewports).
- **Solution**: Converted `.skills-filter-container` and `.admin-nav-tabs` into horizontal touch-scroll carousels (`flex-wrap: nowrap; overflow-x: auto; -webkit-overflow-scrolling: touch; scrollbar-width: none;`). Made Hero CTA buttons full-width touch targets (`min-height: 48px`).

### 4.4 Online Resume Management System
- **Problem**: No static PDF resume existed on disk, requiring dynamic cloud URL support.
- **Solution**: Created dynamic resume management in `frontend/admin.html` allowing Arun to save and test any Google Drive/Dropbox PDF URL, alongside an interactive digital resume summary modal in `frontend/index.html`.

---

## 5. Security & Credentials Architecture
- **Authentication**: Stateless JWT token authentication via Spring Security 6 & JJWT (`0.12.6`).
- **Endpoints**:
  - Public: `GET /api/projects`, `GET /api/skills`, `GET /api/education`, `POST /api/contact`, `GET /api/settings/resume`, `GET /api/health`.
  - Admin (Secured): `POST /api/auth/login`, `GET /api/auth/me`, CRUD on `/api/projects/**`, `/api/skills/**`, `/api/education/**`, `/api/contact/**`, `POST /api/settings/resume`.
- **Environment Isolation**: `.env` and sensitive secrets are strictly untracked via `.gitignore`, with template provided in `.env.example`.
