document.addEventListener("DOMContentLoaded", function() {
    const portalUrl = document.getElementById("portalUrl").value;
    const btnSubmit = document.querySelector(".btn-submit");
    const emailInput = document.querySelector('input[type="email"]');
    const passwordInput = document.querySelector('input[type="password"]');

    btnSubmit.addEventListener("click", function() {
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        if (!email || !password) {
            alert("Please enter both email and password.");
            return;
        }

        fetch(`${portalUrl}/api/spoc/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email: email, password: password })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                
                window.location.href = portalUrl + "/dashboard";
            } else {
                 Swal.fire({
                    icon: 'info',
                    title: 'Login Failed',
                    text: data.message

                }).then(() => {
                    window.location.href = portalUrl;
                });
            }
        })
        .catch(error => {
            console.error("Error logging in:", error);
            Swal.fire({
                    icon: 'info',
                    title: 'Login Failed',
                    text: 'An error occurred while logging in. Please try again.'

                }).then(() => {
                    window.location.href = portalUrl;
                });
            
        });
    });
});
