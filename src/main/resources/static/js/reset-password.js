const password = document.getElementById("password");
const confirmPassword = document.getElementById("confirm");
const submitBtn = document.getElementById("submitBtn");
const matchError = document.getElementById("matchError");
const token = document.getElementById("token")?.value;
const portalUrl = document.getElementById('portalUrl').value;
const rules = {
    len: p => p.length >= 8,
    upper: p => /[A-Z]/.test(p),
    lower: p => /[a-z]/.test(p),
    number: p => /[0-9]/.test(p),
    special: p => /[!@#$%^&*]/.test(p)
};


password.addEventListener("input", validateForm);
confirmPassword.addEventListener("input", validateForm);


document.getElementById("resetForm").addEventListener("submit", function (e) {
    e.preventDefault();
    submitResetPassword();
});

function validateForm() {
    const pass = password.value.trim();
    const confirm = confirmPassword.value.trim();
    let allValid = true;

   
    for (const rule in rules) {
        const valid = rules[rule](pass);
        const el = document.getElementById(rule);

        if (valid) {
            el.classList.add("valid");
            el.classList.remove("invalid");
            el.innerHTML = "✓ " + el.innerText.replace(/^✓ |^✗ /, "");
        } else {
            el.classList.add("invalid");
            el.classList.remove("valid");
            el.innerHTML = "✗ " + el.innerText.replace(/^✓ |^✗ /, "");
            allValid = false;
        }
    }

   
    if (!confirm || pass !== confirm) {
        matchError.style.display = confirm ? "block" : "none";
        allValid = false;
    } else {
        matchError.style.display = "none";
    }

    submitBtn.disabled = !allValid;
    submitBtn.classList.toggle("enabled", allValid);
}

function submitResetPassword() {

    const pass = password.value.trim();
    const confirm = confirmPassword.value.trim();

    if (!pass || !confirm) {
        Swal.fire({
            icon: "warning",
            title: "Missing Fields",
            text: "Both password fields are required"
        });
        return;
    }

    if (pass !== confirm) {
        Swal.fire({
            icon: "error",
            title: "Mismatch",
            text: "Passwords do not match"
        });
        return;
    }

   
    submitBtn.disabled = true;
    showLoader();

    fetch(portalUrl + "/api/public/spoc/reset-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            token: token,
            newPassword: pass
        })
    })
        .then(res => res.json())
        .then(data => {
            hideLoader();
            Swal.close();

            if (data.success) {
                Swal.fire({
                    icon: "success",
                    title: "Success",
                    text: "Password reset successful",
                    confirmButtonText: "Go to Sign In"
                }).then(() => {
                    window.location.href = portalUrl ;
                });
            } else {
                submitBtn.disabled = false;
                Swal.fire({
                    icon: "error",
                    title: "Failed",
                    text: data.message || "Password reset failed. Please try again."
                });
            }
        })
        .catch(err => {
            submitBtn.disabled = false;
            hideLoader();
            console.error(err);

            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Something went wrong. Please try again."
            });
        });
}

function toggle(id) {
    const input = document.getElementById(id);
    input.type = input.type === "password" ? "text" : "password";
}