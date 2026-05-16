package edu.cit.aligato.fortpointproperties.careerapplication.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCareerApplicationDTO {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private MultipartFile resume;

    @NotBlank(message = "Cover letter is required")
    @Size(max = 5000, message = "Cover letter must not exceed 5000 characters")
    private String coverLetter;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MultipartFile getResume() {
        return resume;
    }

    public void setResume(MultipartFile resume) {
        this.resume = resume;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
}
