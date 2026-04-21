package com.payport.payment.security;

import com.payport.payment.model.User;
import com.payport.payment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security calls this automatically during authentication
    // "username" in our app = mobileNumber
    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mobileNumber));

        // Spring's built-in User builder — wraps your entity into a UserDetails object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMobileNumber())
                .password(user.getPassword()) // hashed password stored in DB
                .roles("USER")
                .build();
    }
}