package com.oussama.space_renting.controller;

import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.manager.ManagerLoginRequest;
import com.oussama.space_renting.dto.manager.ManagerRegisterRequest;
import com.oussama.space_renting.dto.user.UserLoginRequest;
import com.oussama.space_renting.dto.user.UserRegisterRequest;
import com.oussama.space_renting.model.Manager.Manager;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.ManagerRepository;
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
@RequestMapping("/api/v1/auth/managers")
@CrossOrigin
public class ManagerAuthController {



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ManagerRegisterRequest request) {
        if (managerRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already used");
        }

        Manager manager = Manager.builder()
                .firstName( request.getFirstName())
                .lastName( request.getLastName())
                .email( request.getEmail())
                .password( passwordEncoder.encode(request.getPassword()))
                .build();


        managerRepository.save(manager);

        return ResponseEntity.ok("Manager registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ManagerLoginRequest loginRequest) {
        try {
            /*
             * Authenticate the user using the authentication created by the provider
             * in SecurityConfig
             */
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
        final String jwt = jwtUtil.generateToken(userDetails, Map.of("role", "MANAGER"));

        return ResponseEntity.ok( AuthResponse.builder().message("Login Successful").token(jwt).build());
    }


    private final ManagerRepository managerRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("managerDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public ManagerAuthController( ManagerRepository managerRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }


}
