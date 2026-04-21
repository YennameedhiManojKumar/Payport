package com.payport.payment.controller;

import com.payport.payment.dto.request.RegisterLinkRequest;
import com.payport.payment.dto.request.AuthRequest;
import com.payport.payment.dto.response.AuthResponse;
import com.payport.payment.model.Account;
import com.payport.payment.model.User;
import com.payport.payment.security.JwtUtil;
import com.payport.payment.service.AccountService;
import com.payport.payment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterLinkRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(request.getPassword());
        user = userService.registerUser(user);

        accountService.linkAccount(user.getId(), request.getUpiId(), request.getBalance());

        String token = jwtUtil.generateToken(user.getMobileNumber());
        return ResponseEntity.ok(new AuthResponse(token, user.getMobileNumber(), user.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMobileNumber(),
                        request.getPassword()));

        User user = userService.findByMobileNumber(request.getMobileNumber());
        String token = jwtUtil.generateToken(user.getMobileNumber());
        return ResponseEntity.ok(new AuthResponse(token, user.getMobileNumber(), user.getName()));
    }
}