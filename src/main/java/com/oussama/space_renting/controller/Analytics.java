package com.oussama.space_renting.controller;


import com.oussama.space_renting.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private AnalyticsService analyticsService;

    public Analytics(
            AnalyticsService analyticsService
    ) {
        this.analyticsService = analyticsService;
    }

}
