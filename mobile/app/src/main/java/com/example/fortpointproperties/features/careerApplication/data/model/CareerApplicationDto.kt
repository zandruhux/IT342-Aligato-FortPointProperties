package com.example.fortpointproperties.features.careerApplication.data.model

data class CareerApplicationDto(
    val id: String? = null,
    val userId: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val resumeUrl: String? = null,
    val resumeOriginalFilename: String? = null,
    val coverLetter: String? = null,
    val status: CareerApplicationStatusDto? = null,
    val submittedAt: String? = null,
    val reviewedAt: String? = null,
    val reviewedBy: String? = null,
    val adminRemarks: String? = null
)
