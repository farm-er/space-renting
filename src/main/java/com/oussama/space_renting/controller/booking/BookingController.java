package com.oussama.space_renting.controller.booking;


import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import com.oussama.space_renting.service.BookingService;
import com.oussama.space_renting.service.SpaceService;
import com.oussama.space_renting.service.UserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Operations for managing bookings")
public class BookingController {

    @Operation(
            summary = "Gets all bookings satisfying some filters",
            description = "Gets all bookings but with custom filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns page of bookings"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping
    public ResponseEntity<?> getSpaces(

            @Parameter(description = "Requested status")
            @RequestParam(required = false) BookingStatus status,

            @Parameter(description = "Minimum price spent on booking")
            @RequestParam(required = false) BigDecimal minTotal,

            @Parameter(description = "Maximum price spent on booking")
            @RequestParam(required = false) BigDecimal maxTotal,

            @Parameter(description = "Specific renter")
            @RequestParam(required = false) UUID renterId,

            @Parameter(description = "Specific space")
            @RequestParam(required = false) UUID spaceId,

            @Parameter(description = "Specific staff worked on it")
            @RequestParam(required = false) UUID processedBy,

            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir,

            Authentication authentication
    ) {


        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                "createdAt"
        );

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        Page<Booking> bookings = null;

        if (roles.contains("MANAGER")) {
            bookings = bookingService.findSpacesWithFilters(
              status,
              minTotal,
              maxTotal,
              processedBy,
              spaceId,
              renterId,
              pageable
            );
        } else if (roles.contains("STAFF")) {
            bookings = bookingService.findPendingSpaces( pageable);
        } else if (roles.contains("USER")) {

            User user = userService.getUserByEmail( authentication.getName());

            if (!user.getId().equals( renterId)) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You can't get another user's bookings");
            }

            bookings = bookingService.findSpacesByRenterId( renterId, pageable);
        }


        return ResponseEntity.ok( bookings);
    }




    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(
            BookingService bookingService,
            UserService userService
    ) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

}
