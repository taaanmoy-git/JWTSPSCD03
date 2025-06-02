package com.spjwtd02.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.debug("JwtAuthenticationFilter triggered for URI: {}", request.getRequestURI());
        
        //---------------------------------------------------
        String path = request.getRequestURI();
        
        System.out.println("path is:"+path);
     
//        if (path.startsWith("/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        //-----------------------------------------------------

        final String header = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtUtil.extractUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.error("Illegal argument while fetching username from token", e);
            } catch (ExpiredJwtException e) {
                logger.error("JWT token has expired", e);
            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT token format", e);
            } catch (Exception e) {
                logger.error("Unexpected error while parsing JWT token", e);
            }
        } else {
            logger.warn("Authorization header missing or does not begin with Bearer");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            boolean isValidToken = jwtUtil.validateToken(token, userDetails);

            if (isValidToken) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authenticated user '{}', setting SecurityContext", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }
        }
    
        filterChain.doFilter(request, response);
        
    }
}
