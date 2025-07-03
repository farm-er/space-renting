package com.oussama.space_renting.controller.space;

import com.oussama.space_renting.dto.space.CreateSpaceDTO;
import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.exception.SpaceNotFoundException;
import com.oussama.space_renting.exception.StaffNotFoundException;
import com.oussama.space_renting.exception.UserNotFoundException;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import com.oussama.space_renting.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/spaces")
@Tag(name = "Spaces", description = "Operations for managing spaces")
public class SpaceController {


    @Operation(
            summary = "Gets all spaces satisfying some filters",
            description = "Gets all spaces but with custom filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns list of spaces"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping
    public ResponseEntity<?> getSpaces(
            @Parameter(description = "Address to search for (partial match)")
            @RequestParam(required = false) String address,

            @Parameter(description = "Required amenity")
            @RequestParam(required = false) Amenity amenity,

            @Parameter(description = "Minimum price hour per day")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maximum price hour per day")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Minimum area")
            @RequestParam(required = false) BigDecimal minArea,

            @Parameter(description = "Maximum area")
            @RequestParam(required = false) BigDecimal maxArea,

            @Parameter(description = "Minimum capacity (number of people)")
            @RequestParam(required = false) Integer minCapacity,

            @Parameter(description = "Maximum capacity (number of people)")
            @RequestParam(required = false) Integer maxCapacity,

            @Parameter(description = "City name")
            @RequestParam(required = false) String city,

            @Parameter(description = "Country name")
            @RequestParam(required = false) String country,

            @Parameter(description = "Type of space")
            @RequestParam(required = false) SpaceType type,

            @Parameter(description = "Show only available spaces")
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,

            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Sort by field (e.g., 'price', 'area', 'capacity', 'createdAt')")
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir,

            Authentication authentication
    ) {


        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        );

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_MANAGER")
                );

        System.out.println("is manager is: "+isManager);

        Page<Space> spaces = spaceService.findSpacesWithFilters(
                address, amenity, minPrice, maxPrice, minArea, maxArea,
                minCapacity, maxCapacity, city, country, type, availableOnly, !isManager, pageable
        );

        return ResponseEntity.ok(spaces);
    }

    @Operation(
            summary = "get space",
            description = "get space",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returned results successfully"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getSpace(
            @PathVariable UUID id
    ) {
        try {

            Space space = spaceService.getSpaceById( id);

            return ResponseEntity.ok(space);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( SpaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @Operation(
            summary = "Create a new space",
            description = "Creates a space that can be rented by users.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Space created successfully"),
                    @ApiResponse(responseCode = "500", description = "internal server error")

            }
    )

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<?> createSpace(
            @Valid @RequestBody CreateSpaceDTO createSpaceDTO
    ) {

        Space space = Space.builder()
                .name( createSpaceDTO.getName())
                .description( createSpaceDTO.getDescription())
                .spaceType( createSpaceDTO.getType())
                .amenities( createSpaceDTO.getAmenities())
                .country( createSpaceDTO.getCountry())
                .city( createSpaceDTO.getCity())
                .address( createSpaceDTO.getAddress())
                .postalCode( createSpaceDTO.getPostalCode())
                .capacity(createSpaceDTO.getCapacity())
                .area( createSpaceDTO.getArea())
                .pricePerHour( createSpaceDTO.getPricePerHour())
                .discount( createSpaceDTO.getDiscount())
                .longitude( createSpaceDTO.getLongitude())
                .latitude( createSpaceDTO.getLatitude())
                .availableIn( LocalDateTime.now())
                .imageUrls( createSpaceDTO.getImageUrls())
                .build();

        Space savedSpace = spaceService.save( space);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSpace);
    }

    @Operation(
            summary = "Updates space's info",
            description = "Updates space's information, you can only update these fields" +
                    "( name, description, amenities, pricePerHour, area, capacity)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Space updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Space with the provided id was not found"),
                    @ApiResponse(responseCode = "400", description = "Missing id"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpace(
            @PathVariable UUID id,
            @Valid @RequestBody Map<String, Object> updates
    ) {


        try {

            Space space = spaceService.getSpaceById( id);


            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> space.setName( (String) value);
                    case "description" -> space.setDescription( (String) value);
                    case "amenities" -> space.setAmenities((Set<Amenity>) value);
                    case "pricePerHour" -> space.setPricePerHour((BigDecimal) value);
                    case "discount" -> space.setDiscount((BigDecimal) value);
                    case "area" -> space.setArea((BigDecimal) value);
                    case "capacity" -> space.setCapacity((Integer) value);
                }
            });

            Space savedSpace = spaceService.save(space);

            return ResponseEntity.ok(savedSpace);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( SpaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @Operation(
            summary = "Deletes space from db",
            description = "Delete space from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Space deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing id"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpace(
            @PathVariable UUID id
    ) {
        try {

            spaceService.delete( id);

            return ResponseEntity.ok("Space deleted successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        }
    }


    @Operation(
            summary = "Deactivate space",
            description = "Set the space to Inactive so it becomes invisible to normal user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Space deactivated successfully"),
                    @ApiResponse(responseCode = "404", description = "Space with the provided id was not found"),
                    @ApiResponse(responseCode = "400", description = "Missing id"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateSpace(
            @PathVariable UUID id
    ) {


        try {

            spaceService.updateIsActive( id, false);


            return ResponseEntity.ok("Space deactivated successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( SpaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @Operation(
            summary = "Activate space",
            description = "Set the space to active for users to see",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Space activated successfully"),
                    @ApiResponse(responseCode = "404", description = "Space with the provided id was not found"),
                    @ApiResponse(responseCode = "400", description = "Missing id"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateSpace(
            @PathVariable UUID id
    ) {


        try {

            spaceService.updateIsActive( id, true);


            return ResponseEntity.ok("Space activated successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( SpaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    private final SpaceService spaceService;

    public SpaceController(
            SpaceService spaceService
    ) {
        this.spaceService = spaceService;
    }
}

