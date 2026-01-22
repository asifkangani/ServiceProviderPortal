const portalUrl = document.getElementById('portalUrl').value;
document.getElementById("forgotForm").addEventListener("submit", function (e) {
    e.preventDefault(); 

    const emailInput = document.getElementById("email");
    const emailError = document.getElementById("emailError");
    const email = emailInput.value.trim();

   
    emailError.style.display = "none";
    emailInput.classList.remove("error");

    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

   
    if (!email) {
        showError("Email is required");
        return;
    }


    if (!emailRegex.test(email)) {
        showError("Please enter a valid email address");
        return;
    }

    
    showLoader()
    fetch(portalUrl + "/api/public/spoc/forgot-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            email: email
        })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            hideLoader()
            emailInput.value = "";
            Swal.fire({
                icon: "success",
                title: "Success",
                text: data.message
            }).then(() => {
                    window.location.href = portalUrl;
                });
        } else {
            hideLoader()
            Swal.fire({
                icon: "error",
                title: "Error",
                text: data.message
            });
        }
    })
    .catch(err => {
        hideLoader()
        console.error(err);
        Swal.fire({
            icon: "error",
            title: "Oops!",
            text: "Something went wrong. Please try again."
        });
    });

    function showError(msg) {
        emailError.innerText = msg;
        emailError.style.display = "block";
        emailInput.classList.add("error");
    }
});
