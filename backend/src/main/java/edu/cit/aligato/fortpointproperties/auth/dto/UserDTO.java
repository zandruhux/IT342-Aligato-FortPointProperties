package edu.cit.aligato.fortpointproperties.auth.dto;

public class UserDTO {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
    private String profileImageUrl;

    // --- Constructors ---
    public UserDTO() {
    }

    public UserDTO(String id, String email, String firstname, String lastname, String role) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    public UserDTO(String id, String email, String firstname, String lastname, String role, String profileImageUrl) {
        this(id, email, firstname, lastname, role);
        this.profileImageUrl = profileImageUrl;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
