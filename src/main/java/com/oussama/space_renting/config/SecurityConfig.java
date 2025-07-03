package com.oussama.space_renting.config;


import com.oussama.space_renting.security.JwtAuthenticationFilter;
import com.oussama.space_renting.security.custom_user_details.CustomStaffDetailsService;
import com.oussama.space_renting.security.custom_user_details.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationObservationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity( prePostEnabled = true)
public class SecurityConfig {

    /*
     * Password encoder with Bcrypt
     * Using 12 rounds for now
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder( 12);
    }

    /*
     * Our custom DaoAuthenticationProvider with
     * custom user details
     * and custom passwordEncoder
     * Called by config.getAuthenticationManager
     */
    @Bean
    @Qualifier("userAuthenticationProvider")
    public AuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    @Qualifier("staffAuthenticationProvider")
    public AuthenticationProvider staffAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(staffDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /*
     * Function to populate or initialize the Authentication Manager
     * This function calls config.getAuthenticationManager that will ultimately call the AuthenticationProvider from this class
     */

    @Bean
    @Qualifier("userAuthenticationManager")
    @Primary
    public AuthenticationManager userAuthenticationManager() {
        return new ProviderManager(userAuthenticationProvider());
    }

    @Bean
    @Qualifier("staffAuthenticationManager")
    public AuthenticationManager staffAuthenticationManager() {
        return new ProviderManager( staffAuthenticationProvider());
    }


    /*
     * Function to define the filter chain
     * by removing unused filters and adding the used ones
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disabling csrf protection because we're not using sessions
                .csrf(AbstractHttpConfigurer::disable)
                .cors( cors -> cors.configurationSource(corsConfigurationSource()))
                // Exceptions when it comes to applying Auth Filters
                .authorizeHttpRequests(authz -> authz
                        // If we needed to allow another version we can add it here
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/v3/api-docs/swagger-config"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // We don't need sessions with jwt
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Use our own authentication provider
                .authenticationProvider(userAuthenticationProvider())
                .authenticationProvider(staffAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Be very explicit about allowed origins - add your frontend URL
        configuration.setAllowedOriginPatterns(List.of("*"));
        // If you know your frontend URL, use this instead:
        // configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000"));

        // Be explicit about methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Expose headers that frontend might need
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Total-Count"));

        // Allow credentials (cookies, auth headers)
        configuration.setAllowCredentials(true);

        // Cache preflight for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomStaffDetailsService staffDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService,
                          CustomStaffDetailsService staffDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.staffDetailsService = staffDetailsService;
    }

}