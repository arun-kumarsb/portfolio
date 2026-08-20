/**
 * Projects Module
 * Dynamically renders project cards fetched from Spring Boot REST API
 */

(function () {
    const container = document.getElementById('projects-container');

    /**
     * Helper to parse comma-separated technology tags
     */
    function renderTechTags(techString) {
        if (!techString) return '';
        const tags = techString.split(',').map(t => t.trim()).filter(Boolean);
        return tags.map(tag => `<span class="tech-tag">${escapeHtml(tag)}</span>`).join('');
    }

    /**
     * Helper to safely escape HTML
     */
    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    /**
     * Render project cards into grid
     */
    function renderProjects(projects) {
        if (!container) return;

        if (!projects || projects.length === 0) {
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p>No projects available to display at this time.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = projects.map(proj => {
            const isFeatured = Boolean(proj.featured);
            const isSTCS = (proj.title || '').toLowerCase().includes('tactical') || 
                           (proj.title || '').toLowerCase().includes('stcs');
            const githubUrl = proj.githubUrl || 'https://github.com/TODO-arun';
            const liveUrl = proj.liveUrl;

            return `
                <article class="project-card ${isFeatured ? 'featured' : ''}">
                    <div class="project-header">
                        <h3 class="project-title">${escapeHtml(proj.title)}</h3>
                        ${isFeatured ? '<span class="featured-badge">Featured</span>' : ''}
                    </div>

                    <div class="project-body">
                        ${isSTCS ? `
                            <div class="academic-note">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                                <span>Academic Simulation Design</span>
                            </div>
                        ` : ''}
                        
                        <p class="project-description">${escapeHtml(proj.description)}</p>
                        
                        <div class="project-tech-list">
                            ${renderTechTags(proj.technologies)}
                        </div>
                    </div>

                    <div class="project-footer">
                        <a href="${escapeHtml(githubUrl)}" target="_blank" rel="noopener noreferrer" class="btn btn-outline btn-sm">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/></svg>
                            <span>Code</span>
                        </a>

                        ${liveUrl ? `
                            <a href="${escapeHtml(liveUrl)}" target="_blank" rel="noopener noreferrer" class="btn btn-secondary btn-sm">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path><polyline points="15 3 21 3 21 9"></polyline><line x1="10" y1="14" x2="21" y2="3"></line></svg>
                                <span>Demo</span>
                            </a>
                        ` : ''}
                    </div>
                </article>
            `;
        }).join('');
    }

    /**
     * Fetch projects and render
     */
    async function initProjects() {
        if (!container) return;

        try {
            const projects = await window.api.getProjects();
            renderProjects(projects);
        } catch (err) {
            console.error("Failed to load projects:", err);
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p style="color: var(--error);">Unable to load projects. Please check connection.</p>
                </div>
            `;
        }
    }

    // Expose reload function and auto-init
    window.loadProjects = initProjects;
    document.addEventListener('DOMContentLoaded', initProjects);
})();
