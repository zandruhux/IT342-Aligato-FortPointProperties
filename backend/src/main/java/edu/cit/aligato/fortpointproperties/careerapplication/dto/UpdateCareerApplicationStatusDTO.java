package edu.cit.aligato.fortpointproperties.careerapplication.dto;

import jakarta.validation.constraints.Size;

public class UpdateCareerApplicationStatusDTO {

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
