package edu.cit.aligato.fortpointproperties.usermanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.shared.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.shared.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.usermanagement.dto.AdminCreateUserRequestDTO;
import edu.cit.aligato.fortpointproperties.usermanagement.dto.AdminUpdateUserRoleRequestDTO;
import edu.cit.aligato.fortpointproperties.usermanagement.dto.AdminUserResponseDTO;
import edu.cit.aligato.fortpointproperties.usermanagement.service.AdminUserService;
import jakarta.validation.Valid;

@RestController
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/api/admin/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponseDTO>>> getUsers(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "search", required = false) String search) {
        try {
            return ResponseEntity.ok(ApiResponse.success(adminUserService.getUsers(role, search)));
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("USER-001", e.getMessage(), null);
            return new ResponseEntity<>(ApiResponse.error(error), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/admin/users")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> createUser(
            @Valid @RequestBody AdminCreateUserRequestDTO request) {
        try {
            AdminUserResponseDTO user = adminUserService.createUser(request);
            return new ResponseEntity<>(ApiResponse.success(user), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("USER-002", e.getMessage(), null);
            HttpStatus status = e.getMessage().contains("already in use") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(ApiResponse.error(error), status);
        }
    }

    @PutMapping("/api/admin/users/{id}/role")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody AdminUpdateUserRoleRequestDTO request) {
        try {
            AdminUserResponseDTO user = adminUserService.updateUserRole(id, request.getRole());
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("USER-003", e.getMessage(), null);
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(ApiResponse.error(error), status);
        }
    }

    @DeleteMapping("/api/admin/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        try {
            adminUserService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("USER-004", e.getMessage(), null);
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(ApiResponse.error(error), status);
        }
    }
}
