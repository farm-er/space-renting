package com.oussama.space_renting.controller.review;


import com.oussama.space_renting.dto.booking.BookingDTO;
import com.oussama.space_renting.dto.booking.CreateBookingDTO;
import com.oussama.space_renting.dto.review.CreateReviewDTO;
import com.oussama.space_renting.exception.SpaceNotFoundException;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.review.Review;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.service.*;
import com.oussama.space_renting.specification.ReviewSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Operations for managing reviews")
public class ReviewsController {


    @Operation(
            summary = "Gets all reviews satisfying the rating filter",
            description = "Gets all reviews for a rating range",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns page of reviews"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getReviews(
            @Parameter(description = "Minimum rating")
            @RequestParam(required = false) Integer minRating,

            @Parameter(description = "Maximum rating")
            @RequestParam(required = false) Integer maxRating,

            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir

    ) {


        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                "createdAt"
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewsService.findReviewsWithRatingRange(
            minRating,
                maxRating,
                pageable
        );

        return ResponseEntity.ok( reviews);
    }


    @Operation(
            summary = "get reviews of a space",
            description = "get space's reviews",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returned results successfully"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            }
    )
    @GetMapping("/{spaceId}")
    public ResponseEntity<?> getReview(

            @PathVariable UUID spaceId,

            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {


        Sort sort = Sort.by(
                Sort.Direction.DESC,
                "createdAt"
        );

        Pageable pageable = PageRequest.of(page, size, sort);


        Page<Review> reviews = reviewsService.findReviewsBySpace( spaceId, pageable);

        return ResponseEntity.ok(reviews);
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
            @Valid @RequestBody CreateReviewDTO createReviewDTO,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userService.getUserByEmail( email);
        Space space = spaceService.getSpaceById( createReviewDTO.getSpaceId());

        /*
         * check if user used this space before or not
         * and never reviewed this space before
         */

        boolean isEligible = bookingService.existsBookingsForSpaceRentedBy(
                space.getId(),
                user.getId()
        );

        boolean alreadyReviewed = reviewsService.reviewedSpaceBefore(
                space.getId(),
                user.getId()
        );

        if ( !isEligible || alreadyReviewed) {
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED)
                    .body("You need to use this space to be able to review and you can review only one time");
        }

        Review review = Review.builder()
                .reviewer( user)
                .space(space)
                .comment(createReviewDTO.getComment())
                .rating(createReviewDTO.getRating())
                .build();


        Review savedReview = reviewsService.save( review);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }





    private final BookingService bookingService;
    private final ReviewsService reviewsService;
    private final UserService userService;
    private final SpaceService spaceService;

    public ReviewsController(
            BookingService bookingService,
            UserService userService,
            ReviewsService reviewsService,
            SpaceService spaceService
    ) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.spaceService = spaceService;
        this.reviewsService = reviewsService;
    }

}
