package com.oussama.space_renting.controller.user;


import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserRegisterResponseDTO;
import com.oussama.space_renting.exception.UserNotFoundException;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id, Authentication authentication) {

        try {

            User user = userService.getUserById( id);

            boolean hasManagementRole = authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_MANAGER")
                                    || authority.getAuthority().equals("ROLE_STAFF")
                    );

            if ( !hasManagementRole && !user.getEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            UserDTO userDTO = UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .isVerified(user.getIsVerified())
                    .build();
            return ResponseEntity.ok(userDTO);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @GetMapping("/")
    public ResponseEntity<?> getUserByTokenOrUsersForManager(

            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir,

            Authentication authentication
    ) {

        try {

            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            if (roles.contains("ROLE_MANAGER")) {

                Sort sort = Sort.by(
                        sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        "createdAt"
                );

                // Create pageable object
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<User> users = userService.getUsers( pageable);

                return ResponseEntity.ok(users);
            }

            if (roles.contains("ROLE_USER")) {
                User user = userService.getUserByEmail( authentication.getName());

                UserDTO userDTO = UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phoneNumber(user.getPhoneNumber())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .isVerified(user.getIsVerified())
                        .build();
                return ResponseEntity.ok(userDTO);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates,
            Authentication authentication
    ) {

        try {

            User user = userService.getUserById(id);

            boolean hasManagementRole = authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_MANAGER")
                                    || authority.getAuthority().equals("ROLE_STAFF")
                    );

            if ( !hasManagementRole && !user.getEmail().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            updates.forEach((key, value) -> {
                switch (key) {
                    case "firstName" -> user.setFirstName( value);
                    case "lastName" -> user.setLastName( value);
                    case "email" -> user.setEmail( value);
                    case "phoneNumber" -> user.setPhoneNumber( value);
                }
            });

            User savedUser = userService.save(user);

            UserDTO userDTO = UserDTO.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .phoneNumber(savedUser.getPhoneNumber())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .isVerified(savedUser.getIsVerified())
                    .build();

            return ResponseEntity.ok(userDTO);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }


    }


    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

}
