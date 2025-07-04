package com.oussama.space_renting.dto.analytics;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
public class DailyNewUsersDTO {
    private LocalDate date;
    private Long userCount;
}
