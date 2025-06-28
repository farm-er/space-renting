package com.oussama.space_renting.controller;


import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.manager.ManagerRegisterRequest;
import com.oussama.space_renting.dto.staff.StaffLoginRequest;
import com.oussama.space_renting.dto.staff.StaffRegisterRequest;
import com.oussama.space_renting.dto.user.UserLoginRequest;
import com.oussama.space_renting.model.Manager.Manager;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.repository.ManagerRepository;
import com.oussama.space_renting.repository.StaffRepository;
import com.oussama.space_renting.repository.UserRepository;
import com.oussama.space_renting.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/staff")
@CrossOrigin
public class StaffAuthController {


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody StaffRegisterRequest request) {
        if (staffRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already used");
        }

        Staff staff = Staff.builder()
                .firstName( request.getFirstName())
                .lastName( request.getLastName())
                .email( request.getEmail())
                .password( passwordEncoder.encode(request.getPassword()))
                .build();


        staffRepository.save(staff);

        return ResponseEntity.ok("Staff registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody StaffLoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body( AuthResponse.builder().message("Invalid credentials").token(null).build());
        }

        // Load user details and generate token
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getEmail());
        /*
         * Added the role part in claims to use it to differentiate between user/staff/manager
         */
        final String jwt = jwtUtil.generateToken(userDetails, Map.of("role", "STAFF"));

        return ResponseEntity.ok( AuthResponse.builder().message("Login Successful").token(jwt).build());
    }


    private final StaffRepository staffRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("staffDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public StaffAuthController(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

}
