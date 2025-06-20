package com.oussama.space_renting.controller;

import com.oussama.space_renting.dto.CreateSpaceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller for managing rentable spaces.
 */
@RestController
@RequestMapping("/api/spaces")
@Tag(name = "Spaces", description = "Operations for managing rentable spaces")
@Validated
public class SpaceController {

    @PostMapping
    @Operation(
            summary = "Create a new space",
            description = "Creates a space that can be rented by users.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Space created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<CreateSpaceDTO> createSpace(@Valid @RequestBody CreateSpaceDTO createSpaceDTO) {
        // Your business logic to save space would go here
        // For demo, return the same DTO with CREATED status
        return ResponseEntity.status(HttpStatus.CREATED).body(createSpaceDTO);
    }
}

