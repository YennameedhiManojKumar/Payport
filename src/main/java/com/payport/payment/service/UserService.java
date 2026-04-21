package com.payport.payment.service;

import com.payport.payment.model.Account;
import com.payport.payment.model.User;
import com.payport.payment.repository.AccountRepository;
import com.payport.payment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.payport.payment.exception.PayportException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
            throw new PayportException("Mobile number already registered", HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByMobileNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new PayportException("User not found", HttpStatus.NOT_FOUND));
    }

    public Account linkAccount(Long userId, String upiId, double balance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PayportException("User not found", HttpStatus.NOT_FOUND));

        Account account = new Account();
        account.setUpiId(upiId);
        account.setBalance(balance);
        account.setUser(user);

        return accountRepository.save(account);
    }
}