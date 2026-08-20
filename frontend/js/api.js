/**
 * API Utility Layer for Arun's Developer Portfolio
 * Handles all HTTP REST communication between Frontend and Spring Boot Backend
 * Supports Public and Spring Security Protected Admin Endpoints
 */

const API_CONFIG = {
    // Production Render Backend URL (replace with your Render URL when deployed, e.g. 'https://arun-portfolio-api.onrender.com/api')
    PROD_BACKEND_URL: 'https://arun-portfolio-api.onrender.com/api',
    
    // Automatically uses localhost in local development and Render URL in production on Vercel
    get BASE_URL() {
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            return window.location.origin.includes('8080') ? '/api' : 'http://localhost:8080/api';
        }
        return this.PROD_BACKEND_URL;
    },
    TIMEOUT_MS: 8000,
    TOKEN_KEY: 'arun_portfolio_jwt_token',
    USER_KEY: 'arun_portfolio_admin_user'
};

// Built-in Mock Data Fallback in case Backend is starting up or offline
const MOCK_DATA = {
    projects: [
        {
            id: 1,
            title: "SpendSense",
            description: "A personal and group expense tracking application featuring multi-user expense splitting, settlement tracking, category-wise expenditure analytics, and secure local data persistence.",
            technologies: "React Native, Expo, JavaScript",
            githubUrl: "https://github.com/TODO-arun/spendsense",
            liveUrl: "",
            imageUrl: "",
            featured: true
        },
        {
            id: 2,
            title: "Full-Stack Portfolio & API Engine",
            description: "A production-grade developer portfolio powered by a Java Spring Boot monolithic REST API, Spring Security, Spring Data JPA persistence, Jakarta Validation, and MySQL relational database.",
            technologies: "Java, Spring Boot, Spring Security, JWT, Spring Data JPA, MySQL, REST API",
            githubUrl: "https://github.com/TODO-arun/portfolio-backend",
            liveUrl: "",
            imageUrl: "",
            featured: true
        },
        {
            id: 3,
            title: "Secure Tactical Communication & Command System (STCS)",
            description: "[Academic System-Design Simulation] An academic architecture project exploring secure distributed communications, command-and-control concepts, event-driven messaging, and data flow modeling.",
            technologies: "Java, Spring Boot, REST APIs, Distributed Systems, Event Architecture, AI/ML",
            githubUrl: "https://github.com/TODO-arun/stcs-simulation",
            liveUrl: "",
            imageUrl: "",
            featured: false
        }
    ],
    skills: [
        { id: 1, name: "Java", category: "Programming", proficiency: "Advanced" },
        { id: 2, name: "Python", category: "Programming", proficiency: "Intermediate" },
        { id: 3, name: "JavaScript", category: "Programming", proficiency: "Intermediate" },
        { id: 4, name: "C", category: "Programming", proficiency: "Proficient" },
        
        { id: 5, name: "Spring Boot", category: "Backend", proficiency: "Advanced" },
        { id: 6, name: "REST APIs", category: "Backend", proficiency: "Advanced" },
        { id: 7, name: "Spring Data JPA", category: "Backend", proficiency: "Advanced" },
        { id: 8, name: "Hibernate", category: "Backend", proficiency: "Proficient" },

        { id: 9, name: "MySQL", category: "Database", proficiency: "Advanced" },
        { id: 10, name: "SQL", category: "Database", proficiency: "Advanced" },

        { id: 11, name: "HTML5", category: "Frontend", proficiency: "Advanced" },
        { id: 12, name: "CSS3", category: "Frontend", proficiency: "Proficient" },
        { id: 13, name: "Vanilla JavaScript", category: "Frontend", proficiency: "Proficient" },

        { id: 14, name: "Git", category: "Tools", proficiency: "Advanced" },
        { id: 15, name: "GitHub", category: "Tools", proficiency: "Advanced" },
        { id: 16, name: "VS Code", category: "Tools", proficiency: "Proficient" },
        { id: 17, name: "IntelliJ IDEA", category: "Tools", proficiency: "Proficient" },

        { id: 18, name: "Machine Learning", category: "AI/ML", proficiency: "Intermediate" },
        { id: 19, name: "Deep Learning", category: "AI/ML", proficiency: "Intermediate" },
        { id: 20, name: "Neural Networks", category: "AI/ML", proficiency: "Intermediate" },
        { id: 21, name: "Python ML Ecosystem", category: "AI/ML", proficiency: "Intermediate" }
    ],
    education: [
        {
            id: 1,
            institution: "TODO: Engineering Institution / University",
            degree: "Bachelor of Engineering (B.E. / B.Tech)",
            field: "Computer Science & Engineering",
            startDate: "2023",
            endDate: "2027 (Expected)",
            description: "Core coursework in Data Structures, Algorithms, Object-Oriented Programming, Database Management Systems, Computer Networks, Operating Systems, and AI/ML foundations."
        },
        {
            id: 2,
            institution: "TODO: Higher Secondary School",
            degree: "Higher Secondary Certificate (Class XII)",
            field: "Science Stream (Physics, Chemistry, Mathematics, Computer Science)",
            startDate: "2021",
            endDate: "2023",
            description: "Built strong foundations in mathematics, analytical problem solving, and introductory computing."
        }
    ],
    messages: [
        {
            id: 1,
            name: "Recruiter Sample",
            email: "recruiter@sample.com",
            message: "Hello Arun, we were reviewing your full-stack projects and would love to connect!",
            createdAt: new Date().toISOString()
        }
    ]
};

class ApiService {

    getToken() {
        return localStorage.getItem(API_CONFIG.TOKEN_KEY);
    }

    setToken(token) {
        localStorage.setItem(API_CONFIG.TOKEN_KEY, token);
    }

    clearToken() {
        localStorage.removeItem(API_CONFIG.TOKEN_KEY);
        localStorage.removeItem(API_CONFIG.USER_KEY);
    }

    setAdminUser(user) {
        localStorage.setItem(API_CONFIG.USER_KEY, JSON.stringify(user));
    }

    getAdminUser() {
        try {
            return JSON.parse(localStorage.getItem(API_CONFIG.USER_KEY));
        } catch {
            return null;
        }
    }

    isAuthenticated() {
        return Boolean(this.getToken());
    }

    /**
     * Generic fetch wrapper with timeout, token injection, and error handling
     */
    async _request(endpoint, options = {}) {
        const url = `${API_CONFIG.BASE_URL}${endpoint}`;
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT_MS);

        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers,
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                if (response.status === 401) {
                    // Unauthorized
                    if (endpoint !== '/auth/login' && window.location.pathname.includes('admin.html')) {
                        this.clearToken();
                        window.location.reload();
                    }
                }
                const errorMessage = data?.message || `Request failed with status ${response.status}`;
                const error = new Error(errorMessage);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return { data, isLive: true };
        } catch (error) {
            clearTimeout(timeoutId);
            throw error;
        }
    }

    // ==========================================
    // Public Endpoints
    // ==========================================

    async getProjects() {
        try {
            const result = await this._request('/projects');
            return result.data;
        } catch (err) {
            console.warn("[API] Live backend unavailable for /api/projects. Serving baseline data.", err.message);
            return MOCK_DATA.projects;
        }
    }

    async getProjectById(id) {
        try {
            const result = await this._request(`/projects/${id}`);
            return result.data;
        } catch (err) {
            return MOCK_DATA.projects.find(p => p.id === Number(id)) || null;
        }
    }

    async getSkills(category = null) {
        try {
            const query = category && category !== 'ALL' ? `?category=${encodeURIComponent(category)}` : '';
            const result = await this._request(`/skills${query}`);
            return result.data;
        } catch (err) {
            console.warn("[API] Live backend unavailable for /api/skills. Serving baseline data.", err.message);
            return MOCK_DATA.skills;
        }
    }

    async getEducation() {
        try {
            const result = await this._request('/education');
            return result.data;
        } catch (err) {
            console.warn("[API] Live backend unavailable for /api/education. Serving baseline data.", err.message);
            return MOCK_DATA.education;
        }
    }

    async sendContactMessage(payload) {
        return this._request('/contact', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
    }

    async checkBackendHealth() {
        try {
            const result = await this._request('/projects');
            return { online: true, source: 'Spring Boot REST API (Live)' };
        } catch (err) {
            return { online: false, source: 'Local Static Mode (Backend Inactive)' };
        }
    }

    // ==========================================
    // Admin Security & CRUD Endpoints
    // ==========================================

    async login(username, password) {
        const response = await this._request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        if (response.data && response.data.token) {
            this.setToken(response.data.token);
            this.setAdminUser({
                username: response.data.username,
                role: response.data.role
            });
        }
        return response.data;
    }

    async getCurrentAdmin() {
        const response = await this._request('/auth/me');
        return response.data;
    }

    // Projects CRUD
    async createProject(projectData) {
        return (await this._request('/projects', {
            method: 'POST',
            body: JSON.stringify(projectData)
        })).data;
    }

    async updateProject(id, projectData) {
        return (await this._request(`/projects/${id}`, {
            method: 'PUT',
            body: JSON.stringify(projectData)
        })).data;
    }

    async deleteProject(id) {
        return (await this._request(`/projects/${id}`, {
            method: 'DELETE'
        })).data;
    }

    // Skills CRUD
    async createSkill(skillData) {
        return (await this._request('/skills', {
            method: 'POST',
            body: JSON.stringify(skillData)
        })).data;
    }

    async updateSkill(id, skillData) {
        return (await this._request(`/skills/${id}`, {
            method: 'PUT',
            body: JSON.stringify(skillData)
        })).data;
    }

    async deleteSkill(id) {
        return (await this._request(`/skills/${id}`, {
            method: 'DELETE'
        })).data;
    }

    // Education CRUD
    async createEducation(eduData) {
        return (await this._request('/education', {
            method: 'POST',
            body: JSON.stringify(eduData)
        })).data;
    }

    async updateEducation(id, eduData) {
        return (await this._request(`/education/${id}`, {
            method: 'PUT',
            body: JSON.stringify(eduData)
        })).data;
    }

    async deleteEducation(id) {
        return (await this._request(`/education/${id}`, {
            method: 'DELETE'
        })).data;
    }

    // Messages Inbox & Delete
    async getContactMessages() {
        try {
            const result = await this._request('/contact');
            return result.data;
        } catch (err) {
            console.warn("[API] Falling back to baseline messages in preview mode:", err);
            return MOCK_DATA.messages;
        }
    }

    async deleteContactMessage(id) {
        return (await this._request(`/contact/${id}`, {
            method: 'DELETE'
        })).data;
    }
}

// Export singleton API instance
window.api = new ApiService();
