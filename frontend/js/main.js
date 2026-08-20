/**
 * Main UI Controller
 * Handles navigation, mobile menu, scroll spy, and UI interactions
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Footer Year
    const yearEl = document.getElementById('current-year');
    if (yearEl) {
        yearEl.textContent = new Date().getFullYear();
    }

    // 2. Mobile Menu Toggle
    const hamburgerBtn = document.getElementById('hamburger-btn');
    const navMenu = document.getElementById('nav-menu');
    const navLinks = document.querySelectorAll('.nav-link');

    function toggleMenu() {
        if (!hamburgerBtn || !navMenu) return;
        const isActive = navMenu.classList.toggle('is-active');
        hamburgerBtn.classList.toggle('is-active', isActive);
        hamburgerBtn.setAttribute('aria-expanded', isActive);
        document.body.style.overflow = isActive ? 'hidden' : '';
    }

    function closeMenu() {
        if (!hamburgerBtn || !navMenu) return;
        navMenu.classList.remove('is-active');
        hamburgerBtn.classList.remove('is-active');
        hamburgerBtn.setAttribute('aria-expanded', 'false');
        document.body.style.overflow = '';
    }

    if (hamburgerBtn) {
        hamburgerBtn.addEventListener('click', toggleMenu);
    }

    // Close mobile menu on nav link click
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 768) {
                closeMenu();
            }
        });
    });

    // Close mobile menu on Escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && navMenu && navMenu.classList.contains('is-active')) {
            closeMenu();
        }
    });

    // 3. Header Styling on Scroll
    const header = document.getElementById('header');
    function handleScroll() {
        if (!header) return;
        if (window.scrollY > 40) {
            header.style.backgroundColor = 'rgba(10, 13, 20, 0.95)';
            header.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.4)';
        } else {
            header.style.backgroundColor = 'rgba(10, 13, 20, 0.85)';
            header.style.boxShadow = 'none';
        }

        // Active Nav Link Spy
        const sections = document.querySelectorAll('section[id]');
        const scrollPosition = window.scrollY + 120;

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.offsetHeight;
            const sectionId = section.getAttribute('id');

            if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
                navLinks.forEach(link => {
                    const href = link.getAttribute('href');
                    if (href === `#${sectionId}`) {
                        link.classList.add('active');
                    } else {
                        link.classList.remove('active');
                    }
                });
            }
        });
    }

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();

    // 4. Resume Button & Dynamic Online PDF Handling
    const resumeBtn = document.getElementById('resume-btn');
    const resumeModal = document.getElementById('resume-modal');
    const closeResumeBtn = document.getElementById('close-resume-modal');
    const onlineResumeLinkBtn = document.getElementById('online-resume-link-btn');

    if (resumeBtn && resumeModal) {
        resumeBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            const resumeUrl = await window.api.getResumeUrl();
            const isValidOnlineUrl = resumeUrl && resumeUrl.startsWith('http') && !resumeUrl.includes('your-resume-link');

            if (isValidOnlineUrl) {
                // Open configured online PDF/Google Drive link directly in new tab
                window.open(resumeUrl, '_blank');
            } else {
                // If not yet configured with custom cloud link, open the digital interactive resume modal
                if (onlineResumeLinkBtn && isValidOnlineUrl) {
                    onlineResumeLinkBtn.href = resumeUrl;
                    onlineResumeLinkBtn.style.display = 'inline-flex';
                }
                resumeModal.style.display = 'flex';
            }
        });

        if (closeResumeBtn) {
            closeResumeBtn.addEventListener('click', () => {
                resumeModal.style.display = 'none';
            });
        }

        resumeModal.addEventListener('click', (e) => {
            if (e.target === resumeModal) {
                resumeModal.style.display = 'none';
            }
        });
    }
});

