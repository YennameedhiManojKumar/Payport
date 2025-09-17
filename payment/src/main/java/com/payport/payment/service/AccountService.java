package com.payport.payment.service;

import com.payport.payment.model.Account;
import com.payport.payment.model.Transaction;
import com.payport.payment.model.User;
import com.payport.payment.repository.AccountRepository;
import com.payport.payment.repository.TransactionRepository;
import com.payport.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Transfer money between UPI accounts
    public String transfer(String fromUpi, String toUpi, double amount) {
        Account sender = accountRepository.findByUpiId(fromUpi);
        Account receiver = accountRepository.findByUpiId(toUpi);

        if (sender == null || receiver == null) {
            return "❌ Sender or receiver not found!";
        }

        if (sender.getBalance() < amount) {
            return "❌ Insufficient funds!";
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        transactionRepository.save(new Transaction(fromUpi, toUpi, amount, LocalDateTime.now()));

        return "✅ Transfer successful!";
    }

    // ✅ Check account balance
    public double getBalance(String upiId) {
        Account account = accountRepository.findByUpiId(upiId);
        if (account == null) {
            throw new IllegalArgumentException("UPI ID not registered!");
        }
        return account.getBalance();
    }

    // ✅ View transaction history
    public List<Transaction> getTransactionHistory(String upiId) {
        return transactionRepository.findBySenderUpiOrReceiverUpi(upiId, upiId);
    }

    // ✅ Link UPI account to a user
    public Account linkAccount(Long userId, String upiId, double balance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setUpiId(upiId);
        account.setBalance(balance);

        return accountRepository.save(account);
    }
}
