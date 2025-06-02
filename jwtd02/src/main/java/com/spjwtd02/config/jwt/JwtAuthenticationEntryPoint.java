package com.spjwtd02.config.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spjwtd02.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
//import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.warn("Unauthorized access attempt. URI: {}, Message: {}", request.getRequestURI(), authException.getMessage());
        
        ErrorMessage errorMessage = new ErrorMessage(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Access is denied. Please provide a valid JWT token:",
                LocalDateTime.now().toString()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*"); // Optional: for CORS

        mapper.writeValue(response.getOutputStream(), errorMessage);
//        
//        //Another Way
//        PrintWriter writter = response.getWriter();
//        writter.println("Access Denied !"+ authException.getMessage());
    }
}
