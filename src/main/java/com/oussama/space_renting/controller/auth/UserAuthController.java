package com.oussama.space_renting.controller.auth;


import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserLoginRequestDTO;
import com.oussama.space_renting.dto.user.UserRegisterRequestDTO;
import com.oussama.space_renting.dto.user.UserRegisterResponseDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        try {
            /*
             * Authenticate the user using the authentication created by the provider
             * in SecurityConfig
             */
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Load user details and generate token
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
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
        } catch (Exception e) {
            // Log unexpected errors
            System.out.println("Unexpected error during user login" + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(AuthResponse.builder().message("Internal server error").token(null).build());
        }
    }


    /*
     * Endpoint for registering the account
     * Checks for already user email, phone number
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequestDTO request) {

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
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserRegisterResponseDTO.builder()
                            .message("Internal server error")
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

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public UserAuthController(
            UserService userService,
            JwtUtil jwtUtil,
            @Qualifier("userAuthenticationManager") AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }


}
