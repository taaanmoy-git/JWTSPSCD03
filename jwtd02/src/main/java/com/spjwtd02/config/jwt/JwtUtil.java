package com.spjwtd02.config.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Secret key used to sign JWT tokens; loaded from application properties
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // Token expiration time in milliseconds; loaded from application properties
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Generate a JWT token for the authenticated user
    // Called after successful login to issue a token to the client
    public String generateTokenFromUsername(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Can add extra claims here if needed
        String username = userDetails.getUsername();
        return createToken(claims, username);
    }

    // Internal method to create a signed JWT token with claims and username
    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)                  // Set additional token claims
                .setSubject(username)               // Set username as token subject
                .setIssuedAt(now)                   // Set issued timestamp
                .setExpiration(expiryDate)         // Set expiration timestamp
                .signWith(getSigningKey())// Sign the token with secret key
                .compact();
    }

    //------------------------------------------------------------------------
    
	// Convert the Base64-encoded secret key string into a SecretKey object
	// Used internally for signing and verifying tokens
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Validate the token against user details
    // Checks that username matches and token is not expired
    // Used in authentication filters to verify token authenticity
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String usernameFromToken = extractUsernameFromToken(token);
        return (usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    // Extract the username (subject) from the JWT token
    // Used during token validation to identify the user
    public String extractUsernameFromToken(String token) {
    	return extractClaim(token, Claims::getSubject);	
    }

    // Extract the expiration date from the JWT token
    // Used to check if the token is still valid
    public Date extractExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from the token using a resolver function
    // Provides flexibility to get different claims as needed
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims (payload) from the JWT token
    // Used internally by other methods for accessing claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    // Returns true if token expiration date is before current time
    public Boolean isTokenExpired(String token) {
        final Date expiration = extractExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    
}