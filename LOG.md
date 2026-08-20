# Project Activity & Engineering Log (`LOG.md`)

## Summary of All Iterations, Diagnoses, Fixes & Milestones

---

### Milestone 1: Spring Boot Multi-Stage Docker & Render Deployment
- **Objective**: Deploy Spring Boot 3 monolithic REST API to Render cloud container.
- **Issue Diagnosed**: Render dynamic port assignment (`$PORT`) caused initial container restart during port detection.
- **Fix Applied**: Configured `ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]` with fallback `ENV PORT=8080` in `Dockerfile`. Verified live HTTP 200 responses on `https://portfolio-lk3x.onrender.com/api/health`.

---

### Milestone 2: Cloud MySQL (Aiven) DataSource Resilience (`DataSourceConfig.java`)
- **Issue Diagnosed**: Render environment injected MySQL cloud URI in raw `mysql://user:pass@host:port/db?ssl-mode=REQUIRED` format instead of JDBC format (`jdbc:mysql://...`), causing HikariCP "No suitable driver found" warning and fallback to local H2.
- **Fix Applied**: Built smart URI parser in `DataSourceConfig.java` that:
  1. Inspects `SPRING_DATASOURCE_URL` or `DATABASE_URL`.
  2. Parses `mysql://` URI credentials and converts parameters into standard JDBC parameters (`sslmode=require&useSSL=true&allowPublicKeyRetrieval=true`).
  3. Tests connection liveness with 4s validation timeout.
  4. Automatically falls back to in-memory H2 if the cloud database is unavailable, ensuring zero application crashes.

---

### Milestone 3: Cold-Start Grace Window & Live Status Badge (`api.js` & `contact.js`)
- **Issue Diagnosed**: Free tier Render instances take 20–30s to boot from cold sleep. Frontend 8s timeout was triggering false offline fallback.
- **Fix Applied**:
  - Increased `TIMEOUT_MS` to 30,000ms (30s) in `frontend/js/api.js`.
  - Added dynamic status checking in `frontend/js/contact.js` showing `REST API Connected (Live Cloud)` or `REST API Connected (Port 8080)`.

---

### Milestone 4: Comprehensive Mobile & Android Responsiveness Overhaul (`responsive.css`)
- **Issue Diagnosed**: Filter tabs and admin navigation caused horizontal scrollbar overflow and broken wraps on 360px–412px Android viewports.
- **Fix Applied**:
  - Overhauled `frontend/css/responsive.css`.
  - Converted `.skills-filter-container` and `.admin-nav-tabs` into horizontal swipe carousels (`overflow-x: auto; flex-wrap: nowrap; -webkit-overflow-scrolling: touch; scrollbar-width: none;`).
  - Added full-width touch ergonomics for Hero CTA buttons (`min-height: 48px`).
  - Set `viewport-fit=cover` and `maximum-scale=5.0` in HTML meta tags.

---

### Milestone 5: Dynamic Resume Management System
- **Objective**: Provide flexible resume access without static PDF file dependency.
- **Implementation**:
  - Added `getResumeUrl()` and `setResumeUrl()` in `ApiService`.
  - Added interactive Digital Resume Modal in `frontend/index.html` with print-to-PDF support.
  - Added 5th tab `Resume Link Manager` in `frontend/admin.html` with real-time test and save functionality.

---

### Milestone 6: Dedicated Defense & Service Ambition Route (`defense-ambition.html`)
- **Objective**: Articulate Arun's personal background, military lineage, and service aspirations with honesty and technical maturity.
- **Implementation**:
  - Created [`frontend/defense-ambition.html`](file:///C:/Users/iyurs/portfolio/frontend/defense-ambition.html).
  - Framed two distinct, grounded pathways:
    1. *Army Officer Aspirations* (CDS/AFCAT) — Aspiration subject to eligibility and competitive selection.
    2. *Technology for Defense* — Software engineering, distributed systems, and communications architecture.
  - Highlighted academic status of *Secure Tactical Communication & Command System (STCS)* with explicit non-operational simulation notice.
  - Added structured timeline from *Engineering Student* to *Depth Building* to *Defense Opportunities* to *Long-Term Contribution*.

---

### Milestone 7: Narrative Grounding & Removal of Exaggerated Claims
- **Audit & Clean-Up**:
  - Removed all occurrences of the word *"discipline"* and military personality claims.
  - Removed phrases like *"Engineering High-Reliability Systems for National Security"*, *"military specialist"*, *"mission-critical national defense"*.
  - Cleaned all `TODO:` placeholders across HTML and JS, replacing them with real GitHub (`github.com/arun-kumarsb`) and LinkedIn (`linkedin.com/in/arunksb`) links.
  - Cleaned AI/ML & Cybersecurity from main portfolio (`index.html`, `admin.html`, `api.js`, `DataInitializer.java`), preserving them strictly as academic learning topics within `defense-ambition.html`.

---

### Milestone 8: UI Harmonization & Hero Terminal Card Transformation
- **Implementation**:
  - Transformed the personal statement section on `defense-ambition.html` from a basic card into a developer terminal card (`terminal-card`) matching `index.html`.
  - Added traffic light dots (`.dot-red`, `.dot-yellow`, `.dot-green`), terminal header (`arun@defense-station:~/statement.md`), prompt line (`$ cat personal_statement.md`), and pulsing terminal cursor.
  - Equalized card heights across About and Contact sections on desktop.
  - Added mobile navigation drawer to `defense-ambition.html`.

---

## Commit History Record

| Commit Hash | Message Summary | Key Changes |
| :--- | :--- | :--- |
| `50e9714` | Add dedicated Defense and National Security Vision page and navigation routes | Initial creation of `defense-ambition.html` and header links |
| `e41c111` | Remove all AI/ML and Cybersecurity mentions; refocus on backend & high-reliability systems engineering | General codebase clean-up |
| `638a833` | Incorporate Army Brat SIKH LI heritage and DRDO/HAL/IISc defense engineering focus across portfolio | Family heritage & strategic research context |
| `5c8fb9d` | Redesign Defense & Service ambition page and portfolio touchpoints with authentic, mature narrative | Complete narrative grounding, CDS/AFCAT pathways, STCS academic notice, removal of exaggerated claims & TODOs |
| `01f319d` | Remove AI/ML and Cybersecurity from general portfolio while preserving academic exploration on Defense page | Strict separation between main portfolio and defense learning route |
| `343b98d` | fix(ui): harmonize layout, card alignments, margins, and mobile nav; add system docs | Layout harmonization, equal-height cards, and docs creation |
| `63928a4` | style: replace personal statement card with developer terminal card in defense ambition hero | Personal statement terminal card UI upgrade |

