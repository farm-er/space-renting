package com.oussama.space_renting.dto.user;

import com.oussama.space_renting.dto.staff.StaffDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserRegisterResponseDTO {
    String message;
    UserDTO user;
}
