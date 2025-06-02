package com.spjwtd02.controller;

//Extracting the JWT token from the incoming request header.
//Setting it into a new request header when calling the Greeting service.
//Using RestTemplate to forward the request.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/home")
public class HomeController {

	@Autowired
	RestTemplate restTemplate;
	
    @GetMapping
    public String home() {
        return "Welcome to the Home Page. You are authenticated.";
    }
    
    // Note:
//    if I dont use HttpServletRequest
//    in public String getGreetingMessage(HttpServletRequest request)
//    need to use
//    @Autowired
//    private HttpServletRequest request;

    
    //Get message from Greeting service and send request with jwt token
    @RequestMapping(value="/greeting",method= RequestMethod.GET)
    public String getGreetingMessage(HttpServletRequest request) {
    	
    	 // 1. Extract JWT token from incoming request
    	String token = request.getHeader("Authorization"); // Full "Bearer eyJhbGciOi..."

    	//Test
    	System.out.println("token is:"+token);
    	//outp:
    	//token is:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ......
    	
        if (token == null || !token.startsWith("Bearer ")) {
            return "Missing or invalid Authorization header";
        }
    	// Create headers and set Authorization
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", token);
        // When generate token not getting from request
        // String token = jwtUtil.generateTokenFromUsername(userDetails);
        // headers.set("Authorization", "Bearer " + token);  //
        
        //m2
//     // Prepare headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.set("Authorization", token);
        
        
        // Wrap headers in HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // 🔁 Replace with your actual Greeting service URL (must be secured if needed)
        String greetingServiceUrl = "http://localhost:8081/greeting";

        // Call Greeting service
        ResponseEntity<String> response = restTemplate.exchange(
                greetingServiceUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
    	return response.getBody();
    }
}

//Note:
// Check AuthenticationController for more details
//🔁 When should you use "Bearer " + token explicitly?
//  Only when you manually manage or build the token yourself, like after login:
//	String token = jwtUtil.generateTokenFromUsername(userDetails);
//	headers.set("Authorization", "Bearer " + token);  
