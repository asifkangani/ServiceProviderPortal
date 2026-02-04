const portalUrl = document.getElementById('portalUrl')?.value || '';
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

       
        });
        validateAll();
    });

    confirmPassInput.addEventListener('input', validateAll);
});


document.getElementById("passwordForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    changePassword(currentPassword, newPassword, confirmPassword);
});

async function changePassword(currentPassword, newPassword, confirmPassword) {
    try {
        showLoader();

        const requestBody = {
            currentPassword,
            newPassword,
            confirmPassword
        };

        const response = await fetch(portalUrl + "/api/change-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(requestBody)
        });


        if (!response.ok) {
            throw new Error("HTTP Error: " + response.status);
        }

        const data = await response.json();
       hideLoader()

        if (data.success) {
            await Swal.fire({
                icon: 'success',
                title: 'Password Changed',
                text: 'Please login again'
            });

            window.location.href = portalUrl;
        }
        else {
            Swal.fire({
                icon: 'info',
                title: 'Info',
                text: data.message || "Password change failed"
            });
        }

    } catch (err) {

        hideLoader()
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Server error. Please try again."
        });
    } finally {
         hideLoader();
    }
}










