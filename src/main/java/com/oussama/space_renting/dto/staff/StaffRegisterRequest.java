package com.oussama.space_renting.dto.staff;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StaffRegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private final String lastName;

    @Email( message= "invalid email", regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email is required")
    private final String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private final String password;

}
