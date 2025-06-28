package com.oussama.space_renting.controller;


import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.user.UserLoginRequest;
import com.oussama.space_renting.dto.user.UserRegisterRequest;
import com.oussama.space_renting.model.User.User;
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
@RequestMapping("/api/v1/auth/users")
@CrossOrigin
public class UserAuthController {


    /*
     * This endpoint is for Login
     * To get a new token based on credentials
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest loginRequest) {
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
        final String jwt = jwtUtil.generateToken(userDetails, Map.of("role", "USER"));

        return ResponseEntity.ok( AuthResponse.builder().message("Login Successful").token(jwt).build());
    }

    /*
     * Endpoint for registering the account
     * Checks for already user email, phone number
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already taken");
        }

        // Create user entity
        User user = User.builder()
                .firstName( request.getFirstName())
                .lastName( request.getLastName())
                .email( request.getEmail())
                .password( passwordEncoder.encode(request.getPassword()))
                .phoneNumber( request.getPhoneNumber())
                // true for now .. we need to add email verification after
                // and maybe also phone number verification
                .isVerified( false)
                .build();


        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    /*
     * This endpoint is for validating the token
     * Checking if the token is still valid without checking the owner
     * Works if you want to know if you need to refresh your token for example
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            if (jwtUtil.validateToken(token)) {
                return ResponseEntity.ok().body("Token is valid");
            } else {
                return ResponseEntity.badRequest().body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token format");
        }
    }

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public UserAuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


}
