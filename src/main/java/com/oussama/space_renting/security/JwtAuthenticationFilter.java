package com.oussama.space_renting.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /*
     * Loading our jwt utils
     */
    @Autowired
    private JwtUtil jwtUtil;

    /*
     * This service is used as an interface to getting user data
     * TODO: Need to create custom one for our use case
     * This should use the custom service (CustomUserDetailsService) because it's the only one declared
     */
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("staffDetailsService")
    private UserDetailsService staffDetailsService;

    @Autowired
    @Qualifier("managerDetailsService")
    private UserDetailsService managerDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // get the value the auth header
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Get the actual token
            jwt = authorizationHeader.substring(7);
            if ( jwt == null || jwt.trim().isEmpty()) {
                filterChain.doFilter( request, response);
                return;
            }
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                logger.error("JWT token extraction failed", e);
            }
        }

        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter( request, response);
            return;
        }

        // getting the role
        String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

        if ( role == null || role.trim().isEmpty()) {
            filterChain.doFilter( request, response);
            return;
        }

        // populating user details with proper data
        UserDetails userDetails = switch (role) {
            case "USER" -> userDetailsService.loadUserByUsername(email);
            case "MANAGER" -> managerDetailsService.loadUserByUsername(email);
            case "STAFF" -> staffDetailsService.loadUserByUsername(email);
            default -> throw new RuntimeException("Unknown role: " + role);
        };


        // add it to the context
        if (jwtUtil.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // This supposedly adds some details from the request
            // TODO: Check what's added here
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }


        filterChain.doFilter(request, response);
    }
}