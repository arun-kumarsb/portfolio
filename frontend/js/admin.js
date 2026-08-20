/**
 * Admin Panel Controller
 * Handles authentication, tab management, modals, and CRUD operations
 */

(function () {
    // DOM Elements
    const loginSection = document.getElementById('login-section');
    const dashboardSection = document.getElementById('dashboard-section');
    const authControls = document.getElementById('auth-controls');
    const activeUsernameEl = document.getElementById('active-username');
    const loginForm = document.getElementById('login-form');
    const loginBanner = document.getElementById('login-banner');
    const logoutBtn = document.getElementById('logout-btn');

    // Stat Counters
    const statProjects = document.getElementById('stat-projects-count');
    const statSkills = document.getElementById('stat-skills-count');
    const statEducation = document.getElementById('stat-education-count');
    const statMessages = document.getElementById('stat-messages-count');

    // Tables
    const projectsTableBody = document.getElementById('projects-table-body');
    const skillsTableBody = document.getElementById('skills-table-body');
    const educationTableBody = document.getElementById('education-table-body');
    const messagesTableBody = document.getElementById('messages-table-body');

    // Cache local data
    let currentProjects = [];
    let currentSkills = [];
    let currentEducation = [];
    let currentMessages = [];

    // =========================================================================
    // Authentication & View Management
    // =========================================================================

    function updateAuthView() {
        const isAuth = window.api.isAuthenticated();
        if (isAuth) {
            loginSection.style.display = 'none';
            dashboardSection.style.display = 'block';
            authControls.style.display = 'flex';

            const user = window.api.getAdminUser();
            if (activeUsernameEl && user) {
                activeUsernameEl.textContent = user.username || 'admin';
            }

            loadAllDashboardData();
        } else {
            loginSection.style.display = 'flex';
            dashboardSection.style.display = 'none';
            authControls.style.display = 'none';
        }
    }

    async function handleLogin(e) {
        e.preventDefault();
        const usernameInput = document.getElementById('login-username');
        const passwordInput = document.getElementById('login-password');
        const submitBtn = document.getElementById('login-submit-btn');

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        if (!username || !password) return;

        submitBtn.disabled = true;
        if (loginBanner) loginBanner.style.display = 'none';

        try {
            await window.api.login(username, password);
            showToast('success', `Welcome back, ${username}!`);
            updateAuthView();
        } catch (err) {
            console.error("Login failed:", err);
            if (loginBanner) {
                loginBanner.className = 'form-banner error';
                loginBanner.textContent = err.message || 'Invalid admin username or password.';
                loginBanner.style.display = 'block';
            }
        } finally {
            submitBtn.disabled = false;
        }
    }

    function handleLogout() {
        window.api.clearToken();
        showToast('success', 'You have been signed out.');
        updateAuthView();
    }

    // =========================================================================
    // Data Loading & Stats
    // =========================================================================

    async function loadAllDashboardData() {
        await Promise.allSettled([
            loadProjects(),
            loadSkills(),
            loadEducation(),
            loadMessages()
        ]);
        updateStats();
    }

    function updateStats() {
        if (statProjects) statProjects.textContent = currentProjects.length;
        if (statSkills) statSkills.textContent = currentSkills.length;
        if (statEducation) statEducation.textContent = currentEducation.length;
        if (statMessages) statMessages.textContent = currentMessages.length;
    }

    // =========================================================================
    // Projects CRUD
    // =========================================================================

    async function loadProjects() {
        try {
            currentProjects = await window.api.getProjects();
            renderProjectsTable();
        } catch (err) {
            console.error("Error loading projects:", err);
        }
    }

    function renderProjectsTable() {
        if (!projectsTableBody) return;
        if (currentProjects.length === 0) {
            projectsTableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No projects found. Click "+ Add New Project" to create one.</td></tr>`;
            return;
        }

        projectsTableBody.innerHTML = currentProjects.map(p => `
            <tr>
                <td><strong>#${p.id}</strong></td>
                <td><strong>${escapeHtml(p.title)}</strong></td>
                <td><span style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--text-secondary);">${escapeHtml(p.technologies)}</span></td>
                <td>
                    ${p.featured 
                        ? '<span class="featured-badge">Featured</span>' 
                        : '<span style="color: var(--text-muted); font-size: 0.8rem;">Standard</span>'}
                </td>
                <td>
                    <div class="action-btn-group">
                        <button class="btn-action-edit" onclick="window.adminApp.editProject(${p.id})">Edit</button>
                        <button class="btn-action-delete" onclick="window.adminApp.deleteProject(${p.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    function openProjectModal(project = null) {
        const modal = document.getElementById('project-modal');
        const titleEl = document.getElementById('project-modal-title');
        const idInput = document.getElementById('project-id');
        const titleInput = document.getElementById('proj-title');
        const descInput = document.getElementById('proj-desc');
        const techInput = document.getElementById('proj-tech');
        const githubInput = document.getElementById('proj-github');
        const liveInput = document.getElementById('proj-live');
        const featuredInput = document.getElementById('proj-featured');

        if (project) {
            titleEl.textContent = 'Edit Project';
            idInput.value = project.id;
            titleInput.value = project.title || '';
            descInput.value = project.description || '';
            techInput.value = project.technologies || '';
            githubInput.value = project.githubUrl || '';
            liveInput.value = project.liveUrl || '';
            featuredInput.checked = Boolean(project.featured);
        } else {
            titleEl.textContent = 'Add New Project';
            idInput.value = '';
            titleInput.value = '';
            descInput.value = '';
            techInput.value = '';
            githubInput.value = '';
            liveInput.value = '';
            featuredInput.checked = false;
        }

        modal.style.display = 'flex';
    }

    async function handleProjectSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('project-id').value;
        const payload = {
            title: document.getElementById('proj-title').value.trim(),
            description: document.getElementById('proj-desc').value.trim(),
            technologies: document.getElementById('proj-tech').value.trim(),
            githubUrl: document.getElementById('proj-github').value.trim(),
            liveUrl: document.getElementById('proj-live').value.trim(),
            featured: document.getElementById('proj-featured').checked
        };

        try {
            if (id) {
                await window.api.updateProject(id, payload);
                showToast('success', 'Project updated successfully!');
            } else {
                await window.api.createProject(payload);
                showToast('success', 'New project created successfully!');
            }
            closeModal('project-modal');
            await loadProjects();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to save project.');
        }
    }

    async function deleteProject(id) {
        if (!confirm(`Are you sure you want to delete Project #${id}?`)) return;
        try {
            await window.api.deleteProject(id);
            showToast('success', `Project #${id} deleted.`);
            await loadProjects();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to delete project.');
        }
    }

    // =========================================================================
    // Skills CRUD
    // =========================================================================

    async function loadSkills() {
        try {
            currentSkills = await window.api.getSkills();
            renderSkillsTable();
        } catch (err) {
            console.error("Error loading skills:", err);
        }
    }

    function renderSkillsTable() {
        if (!skillsTableBody) return;
        if (currentSkills.length === 0) {
            skillsTableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No skills found. Click "+ Add New Skill" to create one.</td></tr>`;
            return;
        }

        skillsTableBody.innerHTML = currentSkills.map(s => `
            <tr>
                <td><strong>#${s.id}</strong></td>
                <td><strong>${escapeHtml(s.name)}</strong></td>
                <td><span class="skill-category-tag">${escapeHtml(s.category)}</span></td>
                <td><span style="color: var(--text-secondary);">${escapeHtml(s.proficiency)}</span></td>
                <td>
                    <div class="action-btn-group">
                        <button class="btn-action-edit" onclick="window.adminApp.editSkill(${s.id})">Edit</button>
                        <button class="btn-action-delete" onclick="window.adminApp.deleteSkill(${s.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    function openSkillModal(skill = null) {
        const modal = document.getElementById('skill-modal');
        const titleEl = document.getElementById('skill-modal-title');
        const idInput = document.getElementById('skill-id');
        const nameInput = document.getElementById('skill-name');
        const catInput = document.getElementById('skill-cat');
        const profInput = document.getElementById('skill-prof');

        if (skill) {
            titleEl.textContent = 'Edit Skill';
            idInput.value = skill.id;
            nameInput.value = skill.name || '';
            catInput.value = skill.category || 'Programming';
            profInput.value = skill.proficiency || 'Advanced';
        } else {
            titleEl.textContent = 'Add New Skill';
            idInput.value = '';
            nameInput.value = '';
            catInput.value = 'Programming';
            profInput.value = 'Advanced';
        }

        modal.style.display = 'flex';
    }

    async function handleSkillSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('skill-id').value;
        const payload = {
            name: document.getElementById('skill-name').value.trim(),
            category: document.getElementById('skill-cat').value.trim(),
            proficiency: document.getElementById('skill-prof').value.trim()
        };

        try {
            if (id) {
                await window.api.updateSkill(id, payload);
                showToast('success', 'Skill updated successfully!');
            } else {
                await window.api.createSkill(payload);
                showToast('success', 'New skill added successfully!');
            }
            closeModal('skill-modal');
            await loadSkills();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to save skill.');
        }
    }

    async function deleteSkill(id) {
        if (!confirm(`Are you sure you want to delete Skill #${id}?`)) return;
        try {
            await window.api.deleteSkill(id);
            showToast('success', `Skill #${id} deleted.`);
            await loadSkills();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to delete skill.');
        }
    }

    // =========================================================================
    // Education CRUD
    // =========================================================================

    async function loadEducation() {
        try {
            currentEducation = await window.api.getEducation();
            renderEducationTable();
        } catch (err) {
            console.error("Error loading education:", err);
        }
    }

    function renderEducationTable() {
        if (!educationTableBody) return;
        if (currentEducation.length === 0) {
            educationTableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No education records found. Click "+ Add Education Entry" to create one.</td></tr>`;
            return;
        }

        educationTableBody.innerHTML = currentEducation.map(e => `
            <tr>
                <td><strong>#${e.id}</strong></td>
                <td>
                    <strong>${escapeHtml(e.degree)}</strong>
                    ${e.field ? `<div style="font-size: 0.8rem; color: var(--text-muted);">${escapeHtml(e.field)}</div>` : ''}
                </td>
                <td>${escapeHtml(e.institution)}</td>
                <td><span style="font-family: var(--font-mono); font-size: 0.82rem; color: var(--accent-primary);">${escapeHtml(e.startDate || '')} — ${escapeHtml(e.endDate || '')}</span></td>
                <td>
                    <div class="action-btn-group">
                        <button class="btn-action-edit" onclick="window.adminApp.editEducation(${e.id})">Edit</button>
                        <button class="btn-action-delete" onclick="window.adminApp.deleteEducation(${e.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    function openEducationModal(edu = null) {
        const modal = document.getElementById('education-modal');
        const titleEl = document.getElementById('education-modal-title');
        const idInput = document.getElementById('education-id');
        const degreeInput = document.getElementById('edu-degree');
        const fieldInput = document.getElementById('edu-field');
        const instInput = document.getElementById('edu-institution');
        const startInput = document.getElementById('edu-start');
        const endInput = document.getElementById('edu-end');
        const descInput = document.getElementById('edu-desc');

        if (edu) {
            titleEl.textContent = 'Edit Education Entry';
            idInput.value = edu.id;
            degreeInput.value = edu.degree || '';
            fieldInput.value = edu.field || '';
            instInput.value = edu.institution || '';
            startInput.value = edu.startDate || '';
            endInput.value = edu.endDate || '';
            descInput.value = edu.description || '';
        } else {
            titleEl.textContent = 'Add Education Entry';
            idInput.value = '';
            degreeInput.value = '';
            fieldInput.value = '';
            instInput.value = '';
            startInput.value = '';
            endInput.value = '';
            descInput.value = '';
        }

        modal.style.display = 'flex';
    }

    async function handleEducationSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('education-id').value;
        const payload = {
            degree: document.getElementById('edu-degree').value.trim(),
            field: document.getElementById('edu-field').value.trim(),
            institution: document.getElementById('edu-institution').value.trim(),
            startDate: document.getElementById('edu-start').value.trim(),
            endDate: document.getElementById('edu-end').value.trim(),
            description: document.getElementById('edu-desc').value.trim()
        };

        try {
            if (id) {
                await window.api.updateEducation(id, payload);
                showToast('success', 'Education entry updated!');
            } else {
                await window.api.createEducation(payload);
                showToast('success', 'New education entry added!');
            }
            closeModal('education-modal');
            await loadEducation();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to save education entry.');
        }
    }

    async function deleteEducation(id) {
        if (!confirm(`Are you sure you want to delete Education record #${id}?`)) return;
        try {
            await window.api.deleteEducation(id);
            showToast('success', `Education record #${id} deleted.`);
            await loadEducation();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to delete education record.');
        }
    }

    // =========================================================================
    // Contact Messages Inbox
    // =========================================================================

    async function loadMessages() {
        try {
            currentMessages = await window.api.getContactMessages();
            renderMessagesTable();
        } catch (err) {
            console.error("Error loading messages:", err);
        }
    }

    function renderMessagesTable() {
        if (!messagesTableBody) return;
        if (currentMessages.length === 0) {
            messagesTableBody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">No inquiries in your inbox yet.</td></tr>`;
            return;
        }

        messagesTableBody.innerHTML = currentMessages.map(m => {
            const dateStr = m.createdAt ? new Date(m.createdAt).toLocaleString() : 'Recent';
            return `
                <tr>
                    <td><strong>#${m.id}</strong></td>
                    <td><strong>${escapeHtml(m.name)}</strong></td>
                    <td><a href="mailto:${escapeHtml(m.email)}" style="color: var(--accent-primary);">${escapeHtml(m.email)}</a></td>
                    <td style="max-width: 320px; word-break: break-word;">${escapeHtml(m.message)}</td>
                    <td style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--text-muted); white-space: nowrap;">${dateStr}</td>
                    <td>
                        <button class="btn-action-delete" onclick="window.adminApp.deleteMessage(${m.id})">Delete</button>
                    </td>
                </tr>
            `;
        }).join('');
    }

    async function deleteMessage(id) {
        if (!confirm(`Are you sure you want to delete message #${id}?`)) return;
        try {
            await window.api.deleteContactMessage(id);
            showToast('success', `Message #${id} removed from inbox.`);
            await loadMessages();
            updateStats();
        } catch (err) {
            showToast('error', err.message || 'Failed to delete message.');
        }
    }

    // =========================================================================
    // Utilities, Modals & Toast Notifications
    // =========================================================================

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.style.display = 'none';
    }

    function showToast(type, text) {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast-notification ${type}`;
        toast.innerHTML = `
            <span>${type === 'success' ? '✓' : '✕'}</span>
            <span>${escapeHtml(text)}</span>
        `;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(10px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // =========================================================================
    // Event Bindings & Init
    // =========================================================================

    function init() {
        // Auth Listeners
        if (loginForm) loginForm.addEventListener('submit', handleLogin);
        if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

        // Tab Navigation
        const tabButtons = document.querySelectorAll('.tab-btn');
        tabButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetTabId = btn.dataset.tab;
                tabButtons.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');

                document.querySelectorAll('.tab-content').forEach(tc => {
                    tc.classList.remove('active');
                });
                const activeContent = document.getElementById(targetTabId);
                if (activeContent) activeContent.classList.add('active');
            });
        });

        // Add Buttons
        const btnAddProj = document.getElementById('btn-add-project');
        if (btnAddProj) btnAddProj.addEventListener('click', () => openProjectModal());

        const btnAddSkill = document.getElementById('btn-add-skill');
        if (btnAddSkill) btnAddSkill.addEventListener('click', () => openSkillModal());

        const btnAddEdu = document.getElementById('btn-add-education');
        if (btnAddEdu) btnAddEdu.addEventListener('click', () => openEducationModal());

        const btnRefreshMsg = document.getElementById('btn-refresh-messages');
        if (btnRefreshMsg) btnRefreshMsg.addEventListener('click', async () => {
            await loadMessages();
            updateStats();
            showToast('success', 'Inbox refreshed.');
        });

        // Modal Form Submits
        const projForm = document.getElementById('project-form');
        if (projForm) projForm.addEventListener('submit', handleProjectSubmit);

        const skillForm = document.getElementById('skill-form');
        if (skillForm) skillForm.addEventListener('submit', handleSkillSubmit);

        const eduForm = document.getElementById('education-form');
        if (eduForm) eduForm.addEventListener('submit', handleEducationSubmit);

        // Modal Close Buttons
        document.querySelectorAll('[data-close]').forEach(btn => {
            btn.addEventListener('click', () => {
                closeModal(btn.dataset.close);
            });
        });

        // Close modal when clicking outside dialog
        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) overlay.style.display = 'none';
            });
        });

        // Initial Auth check
        updateAuthView();
    }

    // Expose global handlers for inline table button onclicks
    window.adminApp = {
        editProject: (id) => {
            const p = currentProjects.find(item => item.id === id);
            if (p) openProjectModal(p);
        },
        deleteProject,
        editSkill: (id) => {
            const s = currentSkills.find(item => item.id === id);
            if (s) openSkillModal(s);
        },
        deleteSkill,
        editEducation: (id) => {
            const e = currentEducation.find(item => item.id === id);
            if (e) openEducationModal(e);
        },
        deleteEducation,
        deleteMessage
    };

    document.addEventListener('DOMContentLoaded', init);
})();
