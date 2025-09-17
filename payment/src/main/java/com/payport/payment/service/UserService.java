package com.payport.payment.service;

import com.payport.payment.model.Account;
import com.payport.payment.model.User;
import com.payport.payment.repository.AccountRepository;
import com.payport.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private AccountRepository accountRepository;

    // Register a new user
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    // Link a UPI account to an existing user
    public Account linkAccount(Long userId, String upiId, double balance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUpiId(upiId);
        account.setBalance(balance);
        account.setUser(user);

        return accountRepository.save(account);
    }
}
