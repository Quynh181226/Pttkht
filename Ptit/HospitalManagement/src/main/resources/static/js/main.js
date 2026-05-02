// ============================================
// HOSPITAL MANAGEMENT SYSTEM - MAIN JS
// Mobile menu, form validation, confirm dialogs
// ============================================

document.addEventListener('DOMContentLoaded', function() {

    // ===== Mobile Menu Toggle =====
    const mobileToggle = document.getElementById('mobileToggle');
    const navMenu = document.getElementById('navMenu');

    if (mobileToggle && navMenu) {
        mobileToggle.addEventListener('click', function() {
            navMenu.classList.toggle('show');
        });
    }

    // ===== Confirm Cancel Appointment =====
    const cancelButtons = document.querySelectorAll('.btn-cancel-appointment');
    cancelButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('Bạn có chắc chắn muốn hủy lịch hẹn này không?')) {
                e.preventDefault();
            }
        });
    });

    // ===== Confirm Delete (Admin) =====
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('Bạn có chắc chắn muốn xóa mục này không? Hành động này không thể hoàn tác.')) {
                e.preventDefault();
            }
        });
    });

    // ===== Form Validation for Booking =====
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', function(e) {
            const phone = document.getElementById('patientPhone')?.value;
            const phoneRegex = /^\d{10,15}$/;
            if (phone && !phoneRegex.test(phone)) {
                e.preventDefault();
                alert('Số điện thoại phải có 10-15 chữ số');
                return false;
            }

            const email = document.getElementById('patientEmail')?.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (email && email.length > 0 && !emailRegex.test(email)) {
                e.preventDefault();
                alert('Email không hợp lệ');
                return false;
            }
        });
    }

    // ===== Auto-hide alerts after 5 seconds =====
    const alerts = document.querySelectorAll('.alert-success, .alert-error');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

});