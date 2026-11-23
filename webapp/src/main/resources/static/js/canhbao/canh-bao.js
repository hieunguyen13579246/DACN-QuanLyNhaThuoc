'use strict';

document.addEventListener('DOMContentLoaded', () => {
    // Tự động chuyển tab nếu URL có hash (#tabCbTonThap, #tabCbTonCham)
    const hash = window.location.hash;
    if (hash) {
        const triggerEl = document.querySelector(`button[data-bs-target="${hash}"]`);
        if (triggerEl) {
            const tab = new bootstrap.Tab(triggerEl);
            tab.show();
        }
    }
});
