package com.oussama.space_renting.repository;

import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.User.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * This starts an in memory database to use for the tests
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /*
     * I needed to clean the db to avoid conflicts and inconsistencies between tests
     */
    @BeforeEach
    void setUp() {
        // This will delete all users before every test
        userRepository.deleteAll();
    }

    @DisplayName("should return the correct user by email")
    @Test
    void findByEmail_Exists() {

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("123")
                .phoneNumber("123456789")
                .isVerified(false)
                .isActive(true)
                .build();

        userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @DisplayName("should return nothing for an email that doesn't exist")
    @Test
    void findByEmail_DoesntExist() {
        Optional<User> found = userRepository.findByEmail("unknown@example.com");
        assertThat(found).isEmpty();
    }

    @DisplayName("should return false for phone number that doesn't exist")
    @Test
    void existsByPhoneNumber_DoesntExist() {
        boolean result = userRepository.existsByPhoneNumber("123456789");

        assertFalse(result);
    }

    @DisplayName("should return true for existing phone number")
    @Test
    void existsByPhoneNumber_Exists() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("123")
                .phoneNumber("123456789")
                .isVerified(false)
                .isActive(true)
                .build();

        userRepository.save(user);
        boolean result = userRepository.existsByPhoneNumber("123456789");

        assertTrue(result);
    }
}
