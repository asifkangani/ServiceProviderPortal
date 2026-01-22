document.addEventListener('DOMContentLoaded', function () {
    const toggleIcons = document.querySelectorAll('.toggle-password');
    
    toggleIcons.forEach(icon => {
        icon.addEventListener('click', function () {
            const input = this.parentElement.querySelector('input');
            
            if (input.type === "password") {
                input.type = "text";
                this.classList.remove('ph-eye');
                this.classList.add('ph-eye-slash');
            } else {
                input.type = "password";
                this.classList.remove('ph-eye-slash');
                this.classList.add('ph-eye');
            }
        });
    });
});