package edu.cit.aligato.fortpointproperties.properties.dto;

import java.time.LocalDateTime;

public class PropertyAdminDetailDTO extends PropertyAgentDetailDTO {
    public Boolean featured;
    public Boolean visible;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public String createdBy;
}
