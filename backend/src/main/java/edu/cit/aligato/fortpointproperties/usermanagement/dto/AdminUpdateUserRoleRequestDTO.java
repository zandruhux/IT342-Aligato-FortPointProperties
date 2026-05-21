package edu.cit.aligato.fortpointproperties.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminUpdateUserRoleRequestDTO {
    @NotBlank(message = "Role is required")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
