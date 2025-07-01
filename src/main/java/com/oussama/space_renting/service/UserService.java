package com.oussama.space_renting.service;


import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.staff.StaffRegisterRequestDTO;
import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserRegisterRequestDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.exception.UserNotFoundException;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    public UserDTO createUser(UserRegisterRequestDTO request)
            throws EmailAlreadyExistsException {
        // Business logic + validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();

        // I don't I need to handle these errors
        User savedUser = userRepository.save(user);

        return UserDTO.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .isVerified(savedUser.getIsVerified())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    /*
     * Getting user by id
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id.toString()));
    }

    /*
     * Getting user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /*
     * Checks
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }


    public User save(User user) {
        return userRepository.save(user);
    }

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /*
     * Constructor
     */
    public UserService( UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


}
