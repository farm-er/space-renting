package com.oussama.space_renting.controller.auth;


import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.staff.StaffLoginRequestDTO;
import com.oussama.space_renting.dto.staff.StaffRegisterRequestDTO;
import com.oussama.space_renting.dto.staff.StaffRegisterResponseDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.service.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/staff")
@CrossOrigin
@Tag(name = "Authentication", description = "Staff authentication endpoints")
public class StaffAuthController {


    @PostMapping("/register")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> register(@Valid @RequestBody StaffRegisterRequestDTO request, HttpServletRequest httpRequest) {

        try {

            StaffDTO staffDTO = staffService.createStaff( request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body( StaffRegisterResponseDTO.builder()
                            .message("Staff created successfully")
                            .staff( staffDTO)
                            .build()
                    );
        } catch ( EmailAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(StaffRegisterResponseDTO.builder()
                            .message("Email already used")
                            .staff(null)
                            .build()
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body( StaffRegisterResponseDTO.builder()
                            .message("Internal server error")
                            .staff(null)
                            .build()
                    );
        }
    }

    @PostMapping("/login")
    @SecurityRequirements(value = {})
    public ResponseEntity<?> login(@Valid @RequestBody StaffLoginRequestDTO loginRequest) {


        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Load user details and generate token
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            final String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                    .orElse("UNKNOWN");

            /*
             * Added the role part in claims to use it to differentiate between user/staff/manager
             */
            final String jwt = jwtUtil.generateToken(userDetails, Map.of("role", role));

            return ResponseEntity.ok( AuthResponse.builder().message("Login Successful").token(jwt).build());

        }  catch (BadCredentialsException  e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body( AuthResponse.builder().message("Invalid credentials").token(null).build());
        } catch ( NullPointerException e) {
            return ResponseEntity.internalServerError()
                    .body( AuthResponse.builder().message("Internal server error").token(null).build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body( AuthResponse.builder().message("Authentication failed").token(null).build());
        } catch (Exception e) {
            // Log unexpected errors
            System.out.println("Unexpected error during staff login" + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(AuthResponse.builder().message("Internal server error").token(null).build());
        }

    }


    private final StaffService staffService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService staffDetailsService;

    private final JwtUtil jwtUtil;

    public StaffAuthController(
            StaffService staffService,
            JwtUtil jwtUtil,
            @Qualifier("staffDetailsService") UserDetailsService staffDetailsService,
            @Qualifier("staffAuthenticationManager") AuthenticationManager authenticationManager
    ) {
        this.staffService = staffService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.staffDetailsService = staffDetailsService;
    }

}
