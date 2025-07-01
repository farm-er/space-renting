package com.oussama.space_renting.service;


import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.dto.user.UserRegisterRequestDTO;
import com.oussama.space_renting.dto.user.UserRegisterResponseDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository, passwordEncoder);
    }


    @DisplayName("Creating new user throw exception when email is already used")
    @Test
    public void testCreateUser_EmailAlreadyExists() {

        UserRegisterRequestDTO user = UserRegisterRequestDTO.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .phoneNumber("+212897887987")
                .build();

        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn( true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(user),
                "Expected createUser to throw but it didn't"
        );
    }

    @DisplayName("Creating new user is completed successfully")
    @Test
    public void testCreateUser_EmailDoesntExist() {

        UserRegisterRequestDTO userRequest = UserRegisterRequestDTO.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .phoneNumber("+212897887987")
                .build();


        User savedUser = User.builder()
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .id(UUID.randomUUID())
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .phoneNumber("+212897887987")
                .build();

        Mockito.when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn( false);
        Mockito.when(userRepository.save( any(User.class))).thenReturn( savedUser);

        UserDTO userDTO = userService.createUser(userRequest);


        assertNotNull( userDTO.getId());
        assertEquals( savedUser.getId(), userDTO.getId());
        assertEquals( savedUser.getEmail(), userDTO.getEmail());
        assertEquals( savedUser.getFirstName(), userDTO.getFirstName());
        assertEquals( savedUser.getLastName(), userDTO.getLastName());
        assertEquals( savedUser.getPhoneNumber(), userDTO.getPhoneNumber());

    }

    @DisplayName("Should return user with the provided id")
    @Test
    public void testGetUserById_Found() {

        User user = User.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
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

    @DisplayName("Should save the user to the db")
    @Test
    public void testSaveSuccess() {
        User user = User.builder()
                .firstName("first name")
                .lastName("last name")
                .email("example@gmail.com")
                .password("password")
                .build();
        User expectedSavedUser = User.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .id(UUID.randomUUID())
                .firstName("first name")
                .lastName("last name")
                .email("example@gmail.com")
                .password("password")
                .build();

        Mockito.when( userRepository.save(user)).thenReturn( expectedSavedUser);

        User savedUser = userService.save( user);

        assertEquals( expectedSavedUser.getId(), savedUser.getId());
        assertEquals( expectedSavedUser.getEmail(), savedUser.getEmail());
        assertEquals( expectedSavedUser.getFirstName(), savedUser.getFirstName());
        assertEquals( expectedSavedUser.getLastName(), savedUser.getLastName());
    }

    @DisplayName("Should save the user to the db")
    @Test
    public void testSaveFail() {


        Mockito.when( userRepository.save(null)).thenThrow( IllegalArgumentException.class);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(null),
                "Should throw Illegal argument"
        );

    }

    private UserRepository userRepository;
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;


}
