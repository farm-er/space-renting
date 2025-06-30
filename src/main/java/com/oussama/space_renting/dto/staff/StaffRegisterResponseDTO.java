package com.oussama.space_renting.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class StaffRegisterResponseDTO {
    String message;
    StaffDTO staff;
}
