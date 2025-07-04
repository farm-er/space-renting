package com.oussama.space_renting.dto.analytics;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Builder
@AllArgsConstructor
@Getter
public class AnalyticsSummaryDTO {
    private Integer newUsersLastMonth;
    private Integer activeUsersLastMonth;
    private BigDecimal totalRevenue;
    private BigDecimal revenueGrowthPercent;
}
