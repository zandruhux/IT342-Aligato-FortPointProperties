package edu.cit.aligato.fortpointproperties.auth.dto;

public class UserDTO {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
    private String phoneNumber;
    private String profileImageUrl;

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

    public UserDTO(String id, String email, String firstname, String lastname, String role, String phoneNumber, String profileImageUrl) {
        this(id, email, firstname, lastname, role, profileImageUrl);
        this.phoneNumber = phoneNumber;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
