package edu.cit.aligato.fortpointproperties.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleMobileLoginRequest {

    @NotBlank(message = "Google ID token is required")
    private String idToken;

    public GoogleMobileLoginRequest() {
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
