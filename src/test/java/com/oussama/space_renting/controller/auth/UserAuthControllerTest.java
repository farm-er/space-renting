package com.oussama.space_renting.controller.auth;

import com.oussama.space_renting.config.SecurityConfig;
import com.oussama.space_renting.dto.AuthResponse;
import com.oussama.space_renting.dto.user.UserLoginRequestDTO;
import com.oussama.space_renting.repository.UserRepository;
import com.oussama.space_renting.security.JwtAuthenticationFilter;
import com.oussama.space_renting.security.JwtUtil;
import com.oussama.space_renting.security.custom_user_details.CustomStaffDetailsService;
import com.oussama.space_renting.security.custom_user_details.CustomUserDetailsService;
import com.oussama.space_renting.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(SpringExtension.class)
public class UserAuthControllerTest {

    @BeforeEach
    void setUp() {
        mockAuthentication = Mockito.mock(Authentication.class);
        mockUserDetails = new User(
                "test@example.com",
                "encodedPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        userService = Mockito.mock(UserService.class);
        userAuthController = new UserAuthController(userService, jwtUtil, authenticationManager);

    }

    // Login tests
    @DisplayName("Successful login test")
    @Test
    void login_WithValidCredentials_ShouldReturnJwtToken() throws Exception {

        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO("test@example.com", "password");
        String expectedToken = "EXPECTED_TOKEN";

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername("test@example.com")
                        .password("password")
                        .roles("USER")
                        .build()
        );

        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        Mockito.when(jwtUtil.generateToken( eq(mockUserDetails), anyMap()))
                .thenReturn(expectedToken);

        ResponseEntity<?> response = userAuthController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Login Successful", ((AuthResponse) response.getBody()).getMessage());
        assertEquals(expectedToken, ((AuthResponse) response.getBody()).getToken());

    }



    @Mock( name = "userAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private UserService userService;

    private UserAuthController userAuthController;
    private Authentication mockAuthentication;
    private UserDetails mockUserDetails;
}