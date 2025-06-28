package com.oussama.space_renting.service;


import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.User.UserRole;
import com.oussama.space_renting.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @DisplayName("Should return user with the provided id")
    @Test
    public void testGetUserById_Found() {

        User user = User.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UUID id = UUID.randomUUID();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);

        assertEquals( "first name", result.getFirstName());
        assertEquals( "last name", result.getLastName());
        assertEquals( "email@gmail.com", result.getEmail());
    }

    @DisplayName("Should throw a Runtime Exception for user not found by the respective id")
    @Test
    public void testGetUserById_NotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userService.getUserById(id),
                "Expected getUserById to throw, but it didn’t"
        );
        assertEquals("User not found with id: "+id.toString(), thrown.getMessage());
    }

    @DisplayName("Should return user with the provided email")
    @Test
    public void testGetUserByEmail_Found() {
        UUID id = UUID.randomUUID();
        String email = "email@gmail.com";
        User user = User.builder()
                .id(id)
                .firstName("first name")
                .lastName("last name")
                .email(email)
                .password("password")
                .role(UserRole.USER)
                .build();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertEquals( id, result.getId());
        assertEquals( "first name", result.getFirstName());
        assertEquals( "last name", result.getLastName());
        assertEquals( email, result.getEmail());
    }

    @DisplayName("Should throw a Runtime Exception for user not found by the respective email")
    @Test
    public void testGetUserByEmail_NotFound() {
        UUID id = UUID.randomUUID();
        String email = "email@gmail.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userService.getUserByEmail(email),
                "Expected getUserById to throw, but it didn’t"
        );
        assertEquals("User not found with email: " + email, thrown.getMessage());
    }

    @DisplayName("Should return true for the existing email")
    @Test
    public void testExistsByEmail_Exists() {
        String email = "email@gmail.com";
        Mockito.when( userRepository.existsByEmail( email)).thenReturn( true);
        boolean result = userService.existsByEmail( email);
        assertTrue( result);
    }

    @DisplayName("Should return false for the not existing email")
    @Test
    public void testExistsByEmail_NotExists() {
        String email = "email@gmail.com";
        Mockito.when( userRepository.existsByEmail( email)).thenReturn( false);
        boolean result = userService.existsByEmail( email);
        assertFalse( result);
    }


    @DisplayName("Should return true for the existing phone number")
    @Test
    public void testExistsByPhoneNumber_Exists() {
        String phoneNumber = "12345678";
        Mockito.when( userRepository.existsByPhoneNumber( phoneNumber)).thenReturn( true);
        boolean result = userService.existsByPhoneNumber( phoneNumber);
        assertTrue( result);
    }

    @DisplayName("Should return false for the not existing phone number")
    @Test
    public void testExistsByPhoneNumber_NotExists() {
        String phoneNumber = "12345678";
        Mockito.when( userRepository.existsByPhoneNumber( phoneNumber)).thenReturn( false);
        boolean result = userService.existsByPhoneNumber( phoneNumber);
        assertFalse( result);
    }
}
