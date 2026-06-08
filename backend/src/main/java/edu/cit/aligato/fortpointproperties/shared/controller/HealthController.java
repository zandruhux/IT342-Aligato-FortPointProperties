package edu.cit.aligato.fortpointproperties.shared.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.shared.dto.ApiResponse;

@RestController
@RequestMapping({"/api", ""})
public class HealthController {

    @GetMapping({"/health", "/health/"})
    public ResponseEntity<ApiResponse<String>> health() {
        return new ResponseEntity<>(ApiResponse.success("UP"), HttpStatus.OK);
    }
}
