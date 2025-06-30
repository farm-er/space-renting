package com.oussama.space_renting.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    /*
     * Function to extract the sub of the token which is in our case an Email
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
     * Function to get expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /*
     * Function to extract specific field from the Token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
            throws JwtException, IllegalArgumentException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /*
     * Function for generating tokens without any claims
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /*
     * Overloading the first one
     * Generate a token with email + the provided claims
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return createToken(extraClaims, userDetails.getUsername());
    }

    /*
     * Function to validate the token and check if the user provided is the owner
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /*
     * Function to check if the token is valid without checking the owner
     */
    public Boolean validateToken(String token) {
        try {
            if (token == null) return false;
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    /*
     * Secret and Expiration in ms from the config file
     */

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /*
     * Function to get HMAC-SHA key using the string key from spring boot configuration file
     */
    private SecretKey getSigningKey()
            throws  io.jsonwebtoken.security.WeakKeyException,  io.jsonwebtoken.io.DecodingException {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
     * Function to construct the token
     * gets a map of claims plus the sub field ( email in our case) and adds them to the token
     * sets the creation and expiration dates
     * then signs it and return it
     */
    private String createToken(Map<String, Object> claims, String subject)
            throws  io.jsonwebtoken.security.InvalidKeyException {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Function to extract all claims from the Token
     */
    private Claims extractAllClaims(String token)
            throws io.jsonwebtoken.JwtException, IllegalArgumentException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Checks if the token's expiration date is older than the current day
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}