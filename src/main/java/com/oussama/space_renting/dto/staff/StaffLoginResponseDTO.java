package com.oussama.space_renting.dto.staff;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StaffLoginResponseDTO {
    private String message;
    private String role;
    private String token;
}
