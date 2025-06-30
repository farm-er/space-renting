package com.oussama.space_renting.dto.staff;

import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.Staff.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class StaffDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private StaffRole role;
    private StaffStatus status;
    private LocalDateTime createdAt;
}
