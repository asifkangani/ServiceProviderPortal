package com.example.organization.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private Long orgId;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "ForgotPasswordDTO{" +
                "email='" + email + '\'' +
                ", orgId=" + orgId +
                '}';
    }
}
