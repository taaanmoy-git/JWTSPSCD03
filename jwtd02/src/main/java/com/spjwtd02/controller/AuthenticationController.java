package com.spjwtd02.controller;

import com.spjwtd02.config.jwt.JwtUtil;
import com.spjwtd02.dto.LoginRequest;
import com.spjwtd02.dto.LoginResponse;
import com.spjwtd02.dto.SignUpRequest;
import com.spjwtd02.entity.User;
import com.spjwtd02.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	 private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	 
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService; 
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
        	System.out.println("Inside try-catch");
        	logger.info("Inside try catch");
        	
        	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());
        	
        	logger.info("authentication:"+authentication);
        	
            authenticationManager.authenticate(authentication);
            logger.info("Authentication successful for user: {}", loginRequest.getUsername());
                  
        } catch (BadCredentialsException e) {
        	throw new BadCredentialsException("Invalid Username or Password!");
        }

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // Generate JWT token
        String jwtToken = jwtUtil.generateTokenFromUsername(userDetails);

        // Extract roles
        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // Convert to comma-separated string


        // Create response
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alluser/{username}")
    public User getMethodName(@PathVariable String username) {
        Optional<User> optionalUser= userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
        	User user= optionalUser.get();
        	return user;
        }else {
        	return null;
        }
        
        
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest request) {
    	System.out.println("Inside /signup");
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
    
}
