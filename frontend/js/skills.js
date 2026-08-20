/**
 * Skills Module
 * Dynamically loads and filters technical skills from Spring Boot API
 */

(function () {
    let allSkills = [];
    let currentCategory = 'ALL';

    const container = document.getElementById('skills-container');
    const filterContainer = document.getElementById('skills-filter');

    /**
     * Map proficiency strings to percentage widths for progress bars
     */
    function getProficiencyPercentage(proficiency) {
        switch ((proficiency || '').toLowerCase()) {
            case 'advanced': return 90;
            case 'proficient': return 80;
            case 'intermediate': return 70;
            case 'beginner': return 50;
            default: return 75;
        }
    }

    /**
     * Render skill cards into container
     */
    function renderSkills(skills) {
        if (!container) return;

        if (!skills || skills.length === 0) {
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p>No skills found in this category.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = skills.map(skill => {
            const percentage = getProficiencyPercentage(skill.proficiency);
            return `
                <div class="skill-card" data-category="${escapeHtml(skill.category)}">
                    <div class="skill-header">
                        <span class="skill-name">${escapeHtml(skill.name)}</span>
                        <span class="skill-category-tag">${escapeHtml(skill.category)}</span>
                    </div>
                    <div class="skill-level-text">${escapeHtml(skill.proficiency || 'Proficient')}</div>
                    <div class="skill-progress-bar">
                        <div class="skill-progress-fill" style="width: ${percentage}%;"></div>
                    </div>
                </div>
            `;
        }).join('');
    }

    /**
     * Filter skills by selected category
     */
    function applyFilter(category) {
        currentCategory = category;
        
        // Update button active state
        if (filterContainer) {
            const buttons = filterContainer.querySelectorAll('.filter-btn');
            buttons.forEach(btn => {
                btn.classList.toggle('active', btn.dataset.category === category);
            });
        }

        if (category === 'ALL') {
            renderSkills(allSkills);
        } else {
            const filtered = allSkills.filter(s => 
                (s.category || '').toLowerCase() === category.toLowerCase()
            );
            renderSkills(filtered);
        }
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
     * Initialize skills module
     */
    async function initSkills() {
        if (!container) return;

        // Attach event listeners to filter buttons
        if (filterContainer) {
            filterContainer.addEventListener('click', (e) => {
                const btn = e.target.closest('.filter-btn');
                if (btn && btn.dataset.category) {
                    applyFilter(btn.dataset.category);
                }
            });
        }

        try {
            allSkills = await window.api.getSkills();
            applyFilter('ALL');
        } catch (err) {
            console.error("Failed to load skills:", err);
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p style="color: var(--error);">Unable to load skills. Please check connection.</p>
                </div>
            `;
        }
    }

    // Expose reload function and auto-init
    window.loadSkills = initSkills;
    document.addEventListener('DOMContentLoaded', initSkills);
})();
