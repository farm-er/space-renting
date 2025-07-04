package com.oussama.space_renting.service;


import com.oussama.space_renting.dto.AnalyticsSummaryDTO;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.BookingRepository;
import com.oussama.space_renting.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class AnalyticsService {


    public AnalyticsSummaryDTO getSummary() {

        LocalDate today = LocalDate.now();

        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

        Integer newUsersLastMonth = userService.countAccountCreatedBetween(
                firstDayThisMonth.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        Integer activeUsersLastMonth = bookingService.countBookedUsersBetween(
                firstDayLastMonth.atStartOfDay(),
                firstDayThisMonth.atStartOfDay()
        );


        BigDecimal totalRevenueThisMonth = bookingService.revenueBetween(
            firstDayThisMonth.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        if (totalRevenueThisMonth == null) {
            totalRevenueThisMonth = BigDecimal.ZERO;
        }


        BigDecimal totalRevenueLastMonth = bookingService.revenueBetween(
                firstDayLastMonth.atStartOfDay(),
                firstDayThisMonth.plusDays(1).atStartOfDay()
        );

        if (totalRevenueLastMonth == null) {
            totalRevenueLastMonth = BigDecimal.ZERO;
        }

        BigDecimal revenueGrowth = BigDecimal.ZERO;

        if (totalRevenueLastMonth.compareTo( BigDecimal.ZERO) == 0) {
            revenueGrowth = totalRevenueThisMonth.compareTo(BigDecimal.ZERO) > 0
                    ? BigDecimal.valueOf(100)
                    : BigDecimal.ZERO;
        } else {
            revenueGrowth  = totalRevenueThisMonth.subtract(totalRevenueLastMonth)
                    .divide(totalRevenueLastMonth, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }


        BigDecimal totalRevenue = bookingService.revenueBetween(
                null,
                null
        );
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return AnalyticsSummaryDTO.builder()
                .totalRevenue( totalRevenue)
                .activeUsersLastMonth( activeUsersLastMonth)
                .newUsersLastMonth( newUsersLastMonth)
                .revenueGrowthPercent( revenueGrowth)
                .build();

    }



    private final UserService userService;
    private final BookingService bookingService;

    public AnalyticsService(
            UserService userService,
            BookingService bookingService
    ) {
        this.userService = userService;
        this.bookingService = bookingService;
    }


}
