/**
 * Education Module
 * Renders education and academic background from Spring Boot API
 */

(function () {
    const container = document.getElementById('education-container');

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
     * Format academic timeframe
     */
    function formatTimeframe(start, end) {
        if (start && end) return `${start} — ${end}`;
        if (start) return `${start} — Present`;
        if (end) return end;
        return '';
    }

    /**
     * Render education timeline items
     */
    function renderEducation(items) {
        if (!container) return;

        if (!items || items.length === 0) {
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p>No education details available at this time.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = items.map(edu => {
            const timeframe = formatTimeframe(edu.startDate, edu.endDate);
            const degreeWithField = edu.field ? `${edu.degree} in ${edu.field}` : edu.degree;

            return `
                <div class="education-item">
                    <div class="education-node"></div>
                    <div class="education-card">
                        <div class="education-header-row">
                            <h3 class="education-degree">${escapeHtml(degreeWithField)}</h3>
                            ${timeframe ? `<span class="education-period">${escapeHtml(timeframe)}</span>` : ''}
                        </div>
                        <p class="education-institution"><strong>${escapeHtml(edu.institution)}</strong></p>
                        ${edu.description ? `<p class="education-desc">${escapeHtml(edu.description)}</p>` : ''}
                    </div>
                </div>
            `;
        }).join('');
    }

    /**
     * Fetch education data and render
     */
    async function initEducation() {
        if (!container) return;

        try {
            const education = await window.api.getEducation();
            renderEducation(education);
        } catch (err) {
            console.error("Failed to load education details:", err);
            container.innerHTML = `
                <div class="loading-spinner-wrapper">
                    <p style="color: var(--error);">Unable to load education details. Please check connection.</p>
                </div>
            `;
        }
    }

    // Expose reload function and auto-init
    window.loadEducation = initEducation;
    document.addEventListener('DOMContentLoaded', initEducation);
})();
