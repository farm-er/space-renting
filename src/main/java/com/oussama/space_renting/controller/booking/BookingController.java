package com.oussama.space_renting.controller.booking;


import com.oussama.space_renting.dto.booking.BookingDTO;
import com.oussama.space_renting.dto.booking.CreateBookingDTO;
import com.oussama.space_renting.dto.space.CreateSpaceDTO;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import com.oussama.space_renting.service.BookingService;
import com.oussama.space_renting.service.SpaceService;
import com.oussama.space_renting.service.StaffService;
import com.oussama.space_renting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
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
        Page<BookingDTO> bookings = null;

        if (roles.contains("ROLE_MANAGER")) {
            bookings = bookingService.findBookingsWithFilters(
              status,
              minTotal,
              maxTotal,
              processedBy,
              spaceId,
              renterId,
              pageable
            ).map( booking -> BookingDTO.builder()
                    .id( booking.getId())
                    .createdAt( booking.getCreatedAt())
                    .acceptedAt( booking.getProcessedAt())
                    .cancelledAt( booking.getCancelledAt())
                    .cancellationReason( booking.getCancellationReason())
                    .startTime( booking.getStartTime())
                    .endTime( booking.getEndTime())
                    .totalAmount( booking.getTotalAmount())
                    .status( booking.getStatus())
                    .spaceId( booking.getSpaceId())
                    .renterId( booking.getRenterId())
                    .ProcessedById( booking.getProcessedById())
                    .rejectionReason( booking.getRejectionReason())
                    .build());
        } else if (roles.contains("ROLE_STAFF")) {
            bookings = bookingService.findPendingBookings( pageable)
                    .map( booking -> BookingDTO.builder()
                            .id( booking.getId())
                            .createdAt( booking.getCreatedAt())
                            .acceptedAt( booking.getProcessedAt())
                            .cancelledAt( booking.getCancelledAt())
                            .cancellationReason( booking.getCancellationReason())
                            .startTime( booking.getStartTime())
                            .endTime( booking.getEndTime())
                            .totalAmount( booking.getTotalAmount())
                            .status( booking.getStatus())
                            .spaceId( booking.getSpaceId())
                            .renterId( booking.getRenterId())
                            .ProcessedById( booking.getProcessedById())
                            .rejectionReason( booking.getRejectionReason())
                            .build());

        } else if (roles.contains("ROLE_USER")) {

            User user = userService.getUserByEmail( authentication.getName());

            bookings = bookingService.findBookingsByRenterId( user.getId(), pageable)
                    .map( booking -> BookingDTO.builder()
                            .id( booking.getId())
                            .createdAt( booking.getCreatedAt())
                            .acceptedAt( booking.getProcessedAt())
                            .cancelledAt( booking.getCancelledAt())
                            .cancellationReason( booking.getCancellationReason())
                            .startTime( booking.getStartTime())
                            .endTime( booking.getEndTime())
                            .totalAmount( booking.getTotalAmount())
                            .status( booking.getStatus())
                            .spaceId( booking.getSpaceId())
                            .renterId( booking.getRenterId())
                            .ProcessedById( booking.getProcessedById())
                            .rejectionReason( booking.getRejectionReason())
                            .build());

            bookings.stream().forEach(System.out::println);
        } else {
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }


        return ResponseEntity.ok( bookings);
    }

    @Operation(
            summary = "Book a space",
            description = "space booking",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns the booking info"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> bookSpace(
            @Valid @RequestBody CreateBookingDTO createBookingDTO,
            Authentication authentication
    ) {


        boolean isOverlapping = bookingService.existOverlappingBookings(
                createBookingDTO.getStartTime(),
                createBookingDTO.getEndTime(),
                createBookingDTO.getSpaceId()
        );

        if ( isOverlapping) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This space is already booked for the period specified");
        }

        String email = authentication.getName();

        User user = userService.getUserByEmail( email);
        Space space = spaceService.getSpaceById( createBookingDTO.getSpaceId());

        /*
         * check if this space is booked in this time range
         */


        long hours = Duration.between(
                createBookingDTO.getStartTime(),
                createBookingDTO.getEndTime()
        ).toHours();

        Booking booking = Booking.builder()
                .startTime( createBookingDTO.getStartTime())
                .endTime( createBookingDTO.getEndTime())
                .renter( user)
                .space( space)
                .totalAmount( BigDecimal.valueOf(hours).multiply( space.getPricePerHour()))
                .status( BookingStatus.PENDING)
                .build();

        /*
         * update available in for of the space
         */

        if ( space.getAvailableIn().isBefore( createBookingDTO.getEndTime())) {
            space.setAvailableIn( createBookingDTO.getEndTime());
            spaceService.save( space);
        }



        Booking savedBooking = bookingService.save( booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }



    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(
            @PathVariable UUID bookingId,
            @Parameter(description = "Cancellation reason")
            @RequestParam(required = true) String cancellationReason,
            Authentication authentication
    ) {

        String email = authentication.getName();

        System.out.println("email: "+email);

        if (!userService.existsByEmail( email)) {
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Booking booking = bookingService.getBookingById( bookingId);

        booking.setCancelledAt( LocalDateTime.now());
        booking.setStatus( BookingStatus.CANCELLED);
        booking.setCancellationReason( cancellationReason);

        Booking savedBooking = bookingService.save( booking);

        return ResponseEntity.ok("booking cancelled successfully");
    }


    @PostMapping("/reject/{bookingId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> acceptBooking(
            @PathVariable UUID bookingId,
            @Parameter(description = "rejection reason")
            @RequestParam(required = true) String rejectionReason,
            Authentication authentication
    ) {

        String email = authentication.getName();


        Staff staff = staffService.getStaffByEmail( email);

        Booking booking = bookingService.getBookingById( bookingId);

        booking.setProcessedBy( staff);
        booking.setProcessedAt( LocalDateTime.now());
        booking.setStatus( BookingStatus.REJECTED);
        booking.setRejectionReason( rejectionReason);

        Booking savedBooking = bookingService.save( booking);

        return ResponseEntity.ok("booking rejected successfully");
    }


    @PostMapping("/accept/{bookingId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> rejectBooking(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {

        String email = authentication.getName();


        Staff staff = staffService.getStaffByEmail( email);

        Booking booking = bookingService.getBookingById( bookingId);

        booking.setProcessedBy( staff);
        booking.setProcessedAt( LocalDateTime.now());
        booking.setStatus( BookingStatus.BOOKED);

        Booking savedBooking = bookingService.save( booking);

        return ResponseEntity.ok("booking accepted successfully");
    }

    private final BookingService bookingService;
    private final UserService userService;
    private final StaffService staffService;
    private final SpaceService spaceService;

    public BookingController(
            BookingService bookingService,
            UserService userService,
            StaffService staffService,
            SpaceService spaceService
    ) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.spaceService = spaceService;
        this.staffService = staffService;
    }

}
