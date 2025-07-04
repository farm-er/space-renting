package com.oussama.space_renting.dto.analytics;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
public class RevenuePerDayDTO {
    private LocalDate date;
    private BigDecimal total;
}
