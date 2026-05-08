package edu.cit.aligato.fortpointproperties.favorites.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.dto.FavoriteDTO;
import edu.cit.aligato.fortpointproperties.favorites.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.favorites.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.favorites.service.FavoriteService;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;

/**
 * FavoriteController - Authenticated users can manage their favorite properties
 * Endpoints for adding, removing, and viewing favorite properties
 */
@RestController
@RequestMapping("/user/favorites")
@PreAuthorize("isAuthenticated()")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteService favoriteService, UserRepository userRepository) {
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
    }

    /**
     * Get all favorites for the authenticated user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteDTO>>> getAllFavorites(Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(user);
            ApiResponse<List<FavoriteDTO>> response = ApiResponse.success(favorites);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("FAV-001", e.getMessage(), null);
            ApiResponse<List<FavoriteDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add a property to favorites
     */
    @PostMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<String>> addToFavorites(
            @PathVariable String propertyId,
            Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean added = favoriteService.addToFavorites(user, propertyId);

            if (added) {
                ApiResponse<String> response = ApiResponse.success("Property added to favorites");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                ErrorDetail error = new ErrorDetail("FAV-002", "Property already in favorites", null);
                ApiResponse<String> errorResponse = ApiResponse.error(error);
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("FAV-003", e.getMessage(), null);
            ApiResponse<String> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("FAV-004", e.getMessage(), null);
            ApiResponse<String> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove a property from favorites
     */
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<String>> removeFromFavorites(
            @PathVariable String propertyId,
            Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean removed = favoriteService.removeFromFavorites(user, propertyId);

            if (removed) {
                ApiResponse<String> response = ApiResponse.success("Property removed from favorites");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ErrorDetail error = new ErrorDetail("FAV-005", "Property not in favorites", null);
                ApiResponse<String> errorResponse = ApiResponse.error(error);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("FAV-006", e.getMessage(), null);
            ApiResponse<String> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if a property is favorited by the authenticated user
     */
    @GetMapping("/{propertyId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkIfFavorited(
            @PathVariable String propertyId,
            Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean isFavorited = favoriteService.isFavorited(user, propertyId);
            ApiResponse<Boolean> response = ApiResponse.success(isFavorited);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("FAV-007", e.getMessage(), null);
            ApiResponse<Boolean> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get favorite count for the authenticated user
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getFavoriteCount(Authentication authentication) {
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            long count = favoriteService.getFavoriteCount(user);
            ApiResponse<Long> response = ApiResponse.success(count);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("FAV-008", e.getMessage(), null);
            ApiResponse<Long> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
