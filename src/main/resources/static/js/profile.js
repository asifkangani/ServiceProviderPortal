
document.addEventListener('DOMContentLoaded', function () {
    const newPassInput = document.getElementById('newPassword');
    const confirmPassInput = document.getElementById('confirmPassword');
    const errorSpan = document.getElementById('passwordError');
    const submitBtn = document.getElementById('submitBtn');
    const form = document.getElementById('passwordForm');

    document.querySelectorAll('.toggle-password').forEach(icon => {
        icon.addEventListener('click', function () {
            const input = this.parentElement.querySelector('input');
            if (input.type === "password") {
                input.type = "text";
                this.classList.replace('ph-eye', 'ph-eye-slash');
            } else {
                input.type = "password";
                this.classList.replace('ph-eye-slash', 'ph-eye');
            }
        });
    });

    const requirements = [
        { id: 'len', regex: /.{8,}/ },
        { id: 'upper', regex: /[A-Z]/ },
        { id: 'lower', regex: /[a-z]/ },
        { id: 'number', regex: /[0-9]/ },
        { id: 'special', regex: /[!@#$%^&*(),.?":{}|<>]/ }
    ];

    function validateAll() {
        const val = newPassInput.value;
        const confirmVal = confirmPassInput.value;

        const allRulesMet = requirements.every(req => req.regex.test(val));
        const passwordsMatch = (val === confirmVal) && val.length > 0;

        if (confirmVal.length > 0 && val !== confirmVal) {
            errorSpan.style.display = 'flex';
        } else {
            errorSpan.style.display = 'none';
        }

        if (allRulesMet && passwordsMatch) {
            submitBtn.disabled = false;
            submitBtn.style.opacity = "1";
            submitBtn.style.cursor = "pointer";
        } else {
            submitBtn.disabled = true;
            submitBtn.style.opacity = "0.5";
            submitBtn.style.cursor = "not-allowed";
        }
    }

    newPassInput.addEventListener('input', function () {
        const value = this.value;

        requirements.forEach(req => {
            const element = document.getElementById(req.id);
            const isValid = req.regex.test(value);
            element.classList.toggle('valid', isValid);
            element.classList.toggle('invalid', !isValid);

            // if (isValid) {
            //     element.classList.add('valid');
            // } else {
            //     element.classList.remove('valid');
            // }
        });
        validateAll();
    });

    confirmPassInput.addEventListener('input', validateAll);
});
