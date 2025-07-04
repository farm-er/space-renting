package com.oussama.space_renting.controller.analytics;


import com.oussama.space_renting.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Operations for getting Analytics")
public class Analytics {


    @Operation(
            summary = "gets a summary of analytics",
            description = "To get a broad overview",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns summary"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping("/summary")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getSummary() {

        /*
         * get new customers last month
         * -- user with created at before last month (from start of the month until now)
         */

        /*
         * get active customers last month
         * -- user with booking in the last month (from start of the month until now)
         */

        /*
         * total revenue
         * -- total of total amount in all bookings
         */

        /*
         * growth
         * -- total revenue of this month compared to last month (e.g. june and july)
         */
        return ResponseEntity.ok(analyticsService.getSummary());

    }


    @Operation(
            summary = "gets revenue per day",
            description = "gets revenue per day for the specified period",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns revenue per day"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('MANAGER')")
    ResponseEntity<?> getRevenuePerDay(
            @Parameter(description = "Start of time range")
            @RequestParam(required = true) LocalDate startDate,

            @Parameter(description = "End of time range")
            @RequestParam(required = true) LocalDate endDate
    ) {

        return ResponseEntity.ok(analyticsService.getRevenuePerDay(
                startDate,
                endDate
        ));
    }


    @Operation(
            summary = "gets daily active users",
            description = "gets active that booked per day for the specified period",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns revenue per day"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
            }
    )
    @GetMapping("/activity")
    @PreAuthorize("hasRole('MANAGER')")
    ResponseEntity<?> getDailyActiveUsers(
            @Parameter(description = "Start of time range")
            @RequestParam(required = true) LocalDate startDate,

            @Parameter(description = "End of time range")
            @RequestParam(required = true) LocalDate endDate
    ) {

        return ResponseEntity.ok(analyticsService.getDailyActiveUsers(
                startDate,
                endDate
        ));
    }

    private final AnalyticsService analyticsService;

    public Analytics(
            AnalyticsService analyticsService
    ) {
        this.analyticsService = analyticsService;
    }

}
