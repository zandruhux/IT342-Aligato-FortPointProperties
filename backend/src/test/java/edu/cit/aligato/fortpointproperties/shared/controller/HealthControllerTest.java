package edu.cit.aligato.fortpointproperties.shared.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.cit.aligato.fortpointproperties.shared.dto.ApiResponse;

@ExtendWith(SpringExtension.class)
class HealthControllerTest {

    private final HealthController healthController = new HealthController();

    @Test
    void health_returnsOkWithoutDbAccess() {
        ResponseEntity<ApiResponse<String>> response = healthController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().getData());
    }
}
