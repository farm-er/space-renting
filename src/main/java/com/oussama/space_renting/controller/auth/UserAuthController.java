package com.oussama.space_renting.controller.auth;


import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserLoginRequest;
import com.oussama.space_renting.dto.user.UserRegisterRequestDTO;
import com.oussama.space_renting.dto.user.UserRegisterResponseDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/users")
@CrossOrigin
@Tag(name = "Authentication", description = "User authentication endpoints")
@SecurityRequirements(value = {})
public class UserAuthController {


    /*
     * This endpoint is for Login
     * To get a new token based on credentials
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Login to a user account"
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
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

            // Load user details and generate token
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(loginRequest.getEmail());
            /*
             * Added the role part in claims to use it to differentiate between user/staff/manager
             */
            final String jwt = jwtUtil.generateToken(userDetails, Map.of("role", "USER"));

            return ResponseEntity.ok( AuthResponse.builder().message("Login Successful").token(jwt).build());

        } catch (BadCredentialsException  e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body( AuthResponse.builder().message("Invalid credentials").token(null).build());
        } catch ( NullPointerException e) {
            return ResponseEntity.internalServerError()
                    .body( AuthResponse.builder().message("Internal server error").token(null).build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body( AuthResponse.builder().message("Authentication failed").token(null).build());
        }
    }

    /*
     * Endpoint for registering the account
     * Checks for already user email, phone number
     */
    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            description = "Register a new user account",
            security = {}
    )
    public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO request) {

        try {
            UserDTO userDTO = userService.createUser( request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body( UserRegisterResponseDTO.builder()
                            .message("user created successfully")
                            .user( userDTO)
                            .build()
                    );
        } catch ( EmailAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(UserRegisterResponseDTO.builder()
                            .message("Email already used")
                            .user(null)
                            .build()
                    );
        }


    }

    /*
     * This endpoint is for validating the token
     * Checking if the token is still valid without checking the owner
     * Works if you want to know if you need to refresh your token for example
     */
    @PostMapping("/validate")
    @Operation(
            summary = "Token validation",
            description = "Check if your token is valid",
            security = {}
    )
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
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

    private final UserService userService;

    private AuthenticationManager authenticationManager;

    private UserDetailsService userDetailsService;

    private JwtUtil jwtUtil;

    public UserAuthController(
            UserService userService,
            JwtUtil jwtUtil,
            @Qualifier("userDetailsService") UserDetailsService userDetailsService,
            @Qualifier("userAuthenticationManager") AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }


}
