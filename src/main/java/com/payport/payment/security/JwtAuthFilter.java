package com.payport.payment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter guarantees this runs exactly once per request

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // If no token present, skip — SecurityConfig will reject if endpoint is
        // protected
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip "Bearer " prefix to get the raw token
        final String token = authHeader.substring(7);
        final String mobileNumber = jwtUtil.extractMobileNumber(token);

        // Only proceed if we got a mobile number AND user isn't already authenticated
        if (mobileNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);

            if (jwtUtil.isTokenValid(token)) {
                // Build an authentication object and register it in Spring's SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // no credentials needed post-auth
                        userDetails.getAuthorities() // roles (USER in our case)
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // This is what tells Spring "this request is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Always continue the filter chain — blocking happens in SecurityConfig
        filterChain.doFilter(request, response);
    }
}