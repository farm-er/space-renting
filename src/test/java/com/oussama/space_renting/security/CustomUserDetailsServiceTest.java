package com.oussama.space_renting.security;

import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.User.UserRole;
import com.oussama.space_renting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }


    @DisplayName("Should return the correct user details")
    @Test
    public void loadUserByUsername_Found() {
        String email = "example@gmail.com";
        String password = "123";
        User user = Mockito.mock( User.class);
        /*
         * These functions are used by loadByUsername
         */
        Mockito.when(user.getEmail()).thenReturn( email);
        Mockito.when(user.getPassword()).thenReturn( password);

        Mockito.when(userRepository.findByEmail(email)).thenReturn( Optional.of( user));

        UserDetails result = userDetailsService.loadUserByUsername( email);

        assertEquals( user.getEmail(), result.getUsername());

    }


    @DisplayName("Should throw an exception saying the user doesn't exists")
    @Test
    public void loadUserByUsername_NotFound() {

        String email = "example@gmail.com";
        Mockito.when(userRepository.findByEmail( email)).thenReturn( Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername( email),
                "Excepected to throw usernameNotFound but it didn't"
        );
        assertEquals("User not found", thrown.getMessage());
    }

}
