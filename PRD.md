# Product Requirements Document (`PRD.md`)

## 1. Product Overview & Vision
**Arun's Portfolio Ecosystem** is a production-grade, full-stack personal platform built to showcase Arun's software engineering capabilities, technical projects, academic milestones, and career ambitions.

The platform bridges two interconnected dimensions:
1. **Software Engineering Portfolio**: Highlighting backend craftsmanship in Java 17, Spring Boot 3, REST APIs, MySQL, and distributed systems architecture.
2. **Defense & Service Ambition Hub**: Articulating Arun's genuine personal aspiration toward Indian Armed Forces technical commissioning (CDS/AFCAT) and long-term contributions to strategic defense technology organizations (DRDO, HAL, IISc Projects).

---

## 2. Target Personas & Stakeholders

| Persona | Motivation / Goal | Key Experience Expected |
| :--- | :--- | :--- |
| **Technical Recruiters & Hiring Managers** | Evaluate software engineering depth, code quality, and technical maturity. | Fast-loading UI, clear project breakdowns, GitHub repository links, dynamic resume access, and verified backend status. |
| **Senior Engineers & Engineering Leaders** | Assess architectural understanding, system design fundamentals, and clean code practices. | Layered architecture demonstration (STCS simulation, Portfolio API engine), DTO boundaries, and REST API design. |
| **Defense & Strategic Technology Mentors** | Understand Arun's personal background, military lineage, and service aspirations. | Dedicated `defense-ambition.html` route presenting an authentic, mature, and grounded narrative without exaggerated claims. |
| **Arun (Platform Owner / Admin)** | Manage portfolio content dynamically in real time without code redeployments. | Secure JWT-authenticated Admin CMS Dashboard for managing projects, skills, education, contact messages, and resume links. |

---

## 3. Functional Requirements

### 3.1 Public Portfolio Experience (`index.html`)
- **Hero & Terminal**:
  - Interactive developer introduction with typed terminal code preview.
  - Quick CTA access to Projects, Defense Vision route, and Digital Resume.
- **About Section**:
  - Balanced overview of software engineering focus and personal service inspiration.
  - Modular capability cards (Backend Engineering, Distributed Systems, Database Architecture, Defense Ambition).
- **Technical Skills Showcase**:
  - Dynamic skill rendering from Spring Boot REST API.
  - Interactive category filtering (`All Categories`, `Programming`, `Backend`, `Database`, `Frontend`, `Tools`, `Core CS & Systems`).
  - Smooth horizontal swipe on mobile screens.
- **Projects Showcase**:
  - Dynamic card rendering with tag badges (`Featured`, `Academic System Design Simulation`).
  - Repository and live demo action links.
  - STCS project clearly labeled with academic simulation disclaimer.
- **Education Timeline**:
  - Chronological academic milestones (B.E. Computer Science 2023–2027, Higher Secondary).
- **Direct Contact System**:
  - Interactive contact form with real-time client validation and character counter.
  - Submits asynchronously to `POST /api/contact`.
  - Dynamic backend connectivity status badge.
- **Interactive Digital Resume Modal**:
  - On-screen summary of technical skills, featured projects, and education.
  - "Print / Save PDF" browser trigger.
  - Cloud URL redirect button to external PDF (if configured in Admin).

### 3.2 Dedicated Defense & Service Ambition Route (`defense-ambition.html`)
- **Statement of Purpose**: Articulating Arun's personal connection to the armed forces via his father's 30 years in the SIKH LI.
- **Two Distinct Pathways**:
  - Path 1 (*Long-Term Goal*): Army Officer Aspirations through competitive entry routes (CDS/AFCAT).
  - Path 2 (*Engineering Direction*): Technology for Defense (distributed systems, communication protocols, AI/ML concepts, cybersecurity fundamentals).
- **"Why Defense?" Pillars**: Explaining Personal Connection, Engineering Connection, and Future Possibilities.
- **Academic STCS Highlight**: Exploration of secure communication, command-and-control software architecture, and telemetry flows with prominent academic notice.
- **Career Journey Timeline**: Progression from *Today (Engineering Student)* → *Next (Build Depth)* → *Explore (Defense & Service)* → *Long Term (Contribute & Grow)*.

### 3.3 Admin Control Panel (`admin.html`)
- **Authentication**: Secure login with JWT token issuance, storage in `localStorage`, and session validation.
- **Content Management Tabs**:
  1. *Projects Manager*: CRUD operations for project title, description, technologies, GitHub URL, live demo, and featured status.
  2. *Skills Manager*: CRUD operations for skill name, category, and proficiency level.
  3. *Education Manager*: CRUD operations for institution, degree, field, timeline, and description.
  4. *Messages Inbox*: View received contact inquiries, toggle read status, and delete messages.
  5. *Resume Link Manager*: Configure and preview the external cloud resume PDF URL.

---

## 4. Non-Functional Requirements

### 4.1 Performance & Resilience
- **Zero-Downtime Fallback**: If the Spring Boot backend on Render is sleeping or cold-starting, the frontend must immediately fall back to local `MOCK_DATA` without blank screens or broken layouts.
- **Cold-Start Grace Window**: API requests must maintain a 30-second timeout window to accommodate Render free-tier wake-up times.
- **Lightweight Footprint**: Pure Vanilla JS and CSS with zero external UI framework overhead for sub-second page loads.

### 4.2 Security & Data Protection
- **Stateless JWT Security**: Protected admin endpoints require valid `Bearer <token>` headers.
- **Input Sanitization**: Jakarta Validation on backend (`@Valid`, `@NotBlank`, `@Email`, `@Size`), plus client-side HTML entity escaping (`escapeHtml()`) to prevent XSS.
- **CORS Configuration**: Explicit origin whitelist allowing requests from local development and deployed Vercel domains.

### 4.3 Mobile Ergonomics & Android Optimization
- **Zero Horizontal Overflow**: Guaranteed 100% viewport containment on screens from 320px to 412px (Android standard viewports).
- **Touch Targets**: Minimum 44px–48px interactive target height for all buttons and inputs.
- **Swipe Carousels**: Native horizontal touch scrolling for filter buttons and admin tabs.
