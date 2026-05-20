package edu.cit.aligato.fortpointproperties.careerapplication.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.auth.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.CareerApplicationResponseDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.CreateCareerApplicationDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.UpdateCareerApplicationStatusDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.entity.CareerApplicationStatus;
import edu.cit.aligato.fortpointproperties.careerapplication.service.CareerApplicationService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping
public class CareerApplicationController {

    private final CareerApplicationService careerApplicationService;

    public CareerApplicationController(CareerApplicationService careerApplicationService) {
        this.careerApplicationService = careerApplicationService;
    }

    @PostMapping(value = "/api/career-applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('REGISTERED_USER')")
    public ResponseEntity<ApiResponse<CareerApplicationResponseDTO>> submitCareerApplication(
            @Valid @ModelAttribute CreateCareerApplicationDTO request) {
        try {
            CareerApplicationResponseDTO application = careerApplicationService.submitCareerApplication(request);
            return new ResponseEntity<>(ApiResponse.success(application), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/career-applications/me")
    @PreAuthorize("hasAnyRole('REGISTERED_USER', 'AGENT')")
    public ResponseEntity<ApiResponse<CareerApplicationResponseDTO>> getCurrentUserCareerApplication() {
        try {
            return ResponseEntity.ok(ApiResponse.success(careerApplicationService.getCurrentUserCareerApplication()));
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/admin/career-applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CareerApplicationResponseDTO>>> getAllCareerApplications(
            @RequestParam(required = false) CareerApplicationStatus status) {
        List<CareerApplicationResponseDTO> applications = careerApplicationService.getAllCareerApplications(status);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/api/admin/career-applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CareerApplicationResponseDTO>> getCareerApplicationById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(careerApplicationService.getCareerApplicationById(id)));
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/admin/career-applications/{id}/resume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getCareerApplicationResumeUrl(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(careerApplicationService.createResumeSignedUrl(id)));
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/api/admin/career-applications/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CareerApplicationResponseDTO>> acceptCareerApplication(
            @PathVariable String id,
            @Valid @RequestBody(required = false) UpdateCareerApplicationStatusDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(careerApplicationService.acceptCareerApplication(id, request)));
        } catch (IllegalArgumentException e) {
            return buildReviewErrorResponse(e);
        }
    }

    @PatchMapping("/api/admin/career-applications/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CareerApplicationResponseDTO>> rejectCareerApplication(
            @PathVariable String id,
            @Valid @RequestBody(required = false) UpdateCareerApplicationStatusDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(careerApplicationService.rejectCareerApplication(id, request)));
        } catch (IllegalArgumentException e) {
            return buildReviewErrorResponse(e);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildReviewErrorResponse(IllegalArgumentException e) {
        HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return buildErrorResponse(e, status);
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(IllegalArgumentException e, HttpStatus status) {
        ErrorDetail error = new ErrorDetail("CAREER-APPLICATION", e.getMessage(), null);
        return new ResponseEntity<>(ApiResponse.error(error), status);
    }
}
