/**
 * Contact Form Module
 * Handles client-side validation, live character counting, and POST /api/contact API dispatch
 */

(function () {
    const form = document.getElementById('contact-form');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const messageInput = document.getElementById('message');
    const submitBtn = document.getElementById('submit-btn');
    const btnText = submitBtn ? submitBtn.querySelector('.btn-text') : null;
    const btnSpinner = submitBtn ? submitBtn.querySelector('.btn-spinner') : null;
    const formBanner = document.getElementById('form-banner');
    const charCounter = document.getElementById('char-counter');

    const nameError = document.getElementById('name-error');
    const emailError = document.getElementById('email-error');
    const messageError = document.getElementById('message-error');
    const backendStatusIndicator = document.getElementById('backend-status-indicator');

    const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const MAX_MESSAGE_LENGTH = 2000;

    /**
     * Show/hide field-level error
     */
    function setFieldError(inputEl, errorEl, message) {
        if (!errorEl || !inputEl) return;
        if (message) {
            inputEl.classList.add('input-error');
            errorEl.textContent = message;
        } else {
            inputEl.classList.remove('input-error');
            errorEl.textContent = '';
        }
    }

    /**
     * Validate single field
     */
    function validateField(field) {
        if (field === 'name' && nameInput) {
            const val = nameInput.value.trim();
            if (!val) {
                setFieldError(nameInput, nameError, 'Full name is required.');
                return false;
            } else if (val.length < 2) {
                setFieldError(nameInput, nameError, 'Name must be at least 2 characters.');
                return false;
            }
            setFieldError(nameInput, nameError, '');
            return true;
        }

        if (field === 'email' && emailInput) {
            const val = emailInput.value.trim();
            if (!val) {
                setFieldError(emailInput, emailError, 'Email address is required.');
                return false;
            } else if (!EMAIL_REGEX.test(val)) {
                setFieldError(emailInput, emailError, 'Please enter a valid email address.');
                return false;
            }
            setFieldError(emailInput, emailError, '');
            return true;
        }

        if (field === 'message' && messageInput) {
            const val = messageInput.value.trim();
            if (!val) {
                setFieldError(messageInput, messageError, 'Message is required.');
                return false;
            } else if (val.length > MAX_MESSAGE_LENGTH) {
                setFieldError(messageInput, messageError, `Message cannot exceed ${MAX_MESSAGE_LENGTH} characters.`);
                return false;
            }
            setFieldError(messageInput, messageError, '');
            return true;
        }

        return true;
    }

    /**
     * Display a general banner notification above the form
     */
    function showBanner(type, message) {
        if (!formBanner) return;
        formBanner.className = `form-banner ${type}`;
        formBanner.textContent = message;
        formBanner.style.display = 'block';

        if (type === 'success') {
            setTimeout(() => {
                formBanner.style.display = 'none';
            }, 6000);
        }
    }

    /**
     * Set submitting loading state
     */
    function setLoading(isLoading) {
        if (!submitBtn) return;
        submitBtn.disabled = isLoading;
        if (btnText) btnText.textContent = isLoading ? 'Sending...' : 'Send Message';
        if (btnSpinner) btnSpinner.style.display = isLoading ? 'inline-block' : 'none';
    }

    /**
     * Handle form submission
     */
    async function handleSubmit(e) {
        e.preventDefault();

        // Clear existing banner
        if (formBanner) formBanner.style.display = 'none';

        // Validate all fields
        const isNameValid = validateField('name');
        const isEmailValid = validateField('email');
        const isMsgValid = validateField('message');

        if (!isNameValid || !isEmailValid || !isMsgValid) {
            return;
        }

        const payload = {
            name: nameInput.value.trim(),
            email: emailInput.value.trim(),
            message: messageInput.value.trim()
        };

        setLoading(true);

        try {
            const response = await window.api.sendContactMessage(payload);
            const successMsg = response?.data?.message || 'Thank you! Your message has been received.';
            showBanner('success', `✓ ${successMsg}`);
            
            // Reset form
            form.reset();
            if (charCounter) charCounter.textContent = `0 / ${MAX_MESSAGE_LENGTH}`;
            setFieldError(nameInput, nameError, '');
            setFieldError(emailInput, emailError, '');
            setFieldError(messageInput, messageError, '');
        } catch (error) {
            console.error("Submission failed:", error);
            
            // If backend returned structured validation errors
            if (error.data && error.data.errors && Array.isArray(error.data.errors)) {
                const combined = error.data.errors.map(e => e.message || e).join('; ');
                showBanner('error', `Validation Error: ${combined}`);
            } else {
                showBanner('error', `Error: ${error.message || 'Failed to send message. Please ensure the backend is running.'}`);
            }
        } finally {
            setLoading(false);
        }
    }

    /**
     * Check backend status for UX badge
     */
    async function checkBackend() {
        if (!backendStatusIndicator) return;
        const statusDot = document.querySelector('.system-status-box .status-dot');

        try {
            const health = await window.api.checkBackendHealth();
            if (health.online) {
                backendStatusIndicator.textContent = 'REST API Connected (Port 8080)';
                backendStatusIndicator.style.color = 'var(--success)';
                if (statusDot) {
                    statusDot.className = 'status-dot online';
                }
            } else {
                backendStatusIndicator.textContent = 'Static Preview (Backend not running)';
                backendStatusIndicator.style.color = 'var(--warning)';
                if (statusDot) {
                    statusDot.className = 'status-dot';
                }
            }
        } catch {
            backendStatusIndicator.textContent = 'Offline';
            backendStatusIndicator.style.color = 'var(--error)';
            if (statusDot) {
                statusDot.className = 'status-dot offline';
            }
        }
    }

    /**
     * Initialize event bindings
     */
    function initContact() {
        if (!form) return;

        // Form Submit
        form.addEventListener('submit', handleSubmit);

        // Real-time & blur validation
        if (nameInput) {
            nameInput.addEventListener('blur', () => validateField('name'));
            nameInput.addEventListener('input', () => {
                if (nameInput.classList.contains('input-error')) validateField('name');
            });
        }

        if (emailInput) {
            emailInput.addEventListener('blur', () => validateField('email'));
            emailInput.addEventListener('input', () => {
                if (emailInput.classList.contains('input-error')) validateField('email');
            });
        }

        if (messageInput) {
            messageInput.addEventListener('blur', () => validateField('message'));
            messageInput.addEventListener('input', () => {
                const len = messageInput.value.length;
                if (charCounter) charCounter.textContent = `${len} / ${MAX_MESSAGE_LENGTH}`;
                if (messageInput.classList.contains('input-error')) validateField('message');
            });
        }

        // Check backend health
        checkBackend();
        // Periodically refresh backend connection status
        setInterval(checkBackend, 15000);
    }

    document.addEventListener('DOMContentLoaded', initContact);
})();
