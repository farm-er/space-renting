//package com.oussama.space_renting.controller;
//
//import com.oussama.space_renting.security.JwtUtil;
//import com.oussama.space_renting.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserService userService;
//
////    @PostMapping("/register")
////    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
////        try {
////            // Check if user already exists
////            if (userService.existsByUsername(registerRequest.getUsername())) {
////                return ResponseEntity.badRequest().body("Username is already taken!");
////            }
////
////            // Create new user
////            userService.createUser(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
////
////            // Generate JWT token immediately after registration
////            String token = jwtUtil.generateToken(registerRequest.getUsername());
////
////            return ResponseEntity.ok(new JwtResponse(token, "User registered successfully!"));
////
////        } catch (Exception e) {
////            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
////        }
////    }
////
////    @PostMapping("/login")
////    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
////        try {
////            // Authenticate user
////            Authentication authentication = authenticationManager.authenticate(
////                    new UsernamePasswordAuthenticationToken(
////                            loginRequest.getUsername(),
////                            loginRequest.getPassword()
////                    )
////            );
////
////            // Generate JWT token
////            String token = jwtUtil.generateToken(loginRequest.getUsername());
////
////            return ResponseEntity.ok(new JwtResponse(token, "Login successful!"));
////
////        } catch (AuthenticationException e) {
////            return ResponseEntity.badRequest().body("Invalid credentials");
////        }
////    }
//
////    @PostMapping("/validate")
////    public ResponseEntity<?> validateToken(@RequestBody TokenRequest tokenRequest) {
////        boolean isValid = jwtUtil.validateJwtToken(tokenRequest.getToken());
////
////        if (isValid) {
////            String username = jwtUtil.getAllClaimsFromToken(tokenRequest.getToken()).getSubject();
////            return ResponseEntity.ok(new TokenValidationResponse(true, username));
////        } else {
////            return ResponseEntity.ok(new TokenValidationResponse(false, null));
////        }
////    }
////
////    // DTOs
////    public static class RegisterRequest {
////        private String username;
////        private String password;
////        private String email;
////
////        // Getters and setters
////        public String getUsername() { return username; }
////        public void setUsername(String username) { this.username = username; }
////        public String getPassword() { return password; }
////        public void setPassword(String password) { this.password = password; }
////        public String getEmail() { return email; }
////        public void setEmail(String email) { this.email = email; }
////    }
////
////    public static class LoginRequest {
////        private String username;
////        private String password;
////
////        // Getters and setters
////        public String getUsername() { return username; }
////        public void setUsername(String username) { this.username = username; }
////        public String getPassword() { return password; }
////        public void setPassword(String password) { this.password = password; }
////    }
////
////    public static class TokenRequest {
////        private String token;
////
////        public String getToken() { return token; }
////        public void setToken(String token) { this.token = token; }
////    }
////
////    public static class JwtResponse {
////        private String token;
////        private String type = "Bearer";
////        private String message;
////
////        public JwtResponse(String token) {
////            this.token = token;
////        }
////
////        public JwtResponse(String token, String message) {
////            this.token = token;
////            this.message = message;
////        }
////
////        public String getToken() { return token; }
////        public String getType() { return type; }
////        public String getMessage() { return message; }
////    }
////
////    public static class TokenValidationResponse {
////        private boolean valid;
////        private String username;
////
////        public TokenValidationResponse(boolean valid, String username) {
////            this.valid = valid;
////            this.username = username;
////        }
////
////        public boolean isValid() { return valid; }
////        public String getUsername() { return username; }
////    }
//}
