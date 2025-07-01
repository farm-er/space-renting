package com.oussama.space_renting.controller.user;


import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserRegisterResponseDTO;
import com.oussama.space_renting.exception.UserNotFoundException;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id, Authentication authentication) {

        try {

            User user = userService.getUserById( id);

            if (!user.getEmail().equals(authentication.getName())) {
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
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }


    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates,
            Authentication authentication
    ) {

        try {

            User user = userService.getUserById(id);

            if (!user.getEmail().equals(authentication.getName())) {
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
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }


    }


    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

}
