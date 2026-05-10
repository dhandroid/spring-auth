package com.example.minimalapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Demo API", description = "Sample endpoints for public and private resources")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("/public")
    @Operation(
            summary = "Public endpoint",
            description = "Returns a public sample response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Public response returned")
            }
    )
    public String publicEndpoint() {
        log.info("Inside controller method: publicEndpoint()");
        return "public";
    }

    @GetMapping("/private")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
            summary = "Private endpoint",
            description = "Requires Authorization: Bearer (Auth0 access token)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Private response returned"),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
            }
    )
    public String privateEndpoint() {
        log.info("Inside controller method: privateEndpoint()");
        return "private";
    }
}
