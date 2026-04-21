package com.payport.payment.service;

import com.payport.payment.dto.response.TransactionResponse;
import com.payport.payment.exception.PayportException;
import com.payport.payment.model.Account;
import com.payport.payment.model.Transaction;
import com.payport.payment.model.User;
import com.payport.payment.repository.AccountRepository;
import com.payport.payment.repository.TransactionRepository;
import com.payport.payment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void transfer(String fromUpi, String toUpi, double amount) {
        if (fromUpi.equalsIgnoreCase(toUpi)) {
            throw new PayportException("Sender and receiver cannot be the same", HttpStatus.BAD_REQUEST);
        }

        if (amount <= 0) {
            throw new PayportException("Transfer amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        Account sender = accountRepository.findByUpiId(fromUpi)
                .orElseThrow(() -> new PayportException("Sender UPI ID not found", HttpStatus.NOT_FOUND));

        Account receiver = accountRepository.findByUpiId(toUpi)
                .orElseThrow(() -> new PayportException("Receiver UPI ID not found", HttpStatus.NOT_FOUND));

        if (sender.getBalance() < amount) {
            throw new PayportException("Insufficient balance", HttpStatus.BAD_REQUEST);
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSenderUpi(fromUpi);
        transaction.setReceiverUpi(toUpi);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public double getBalance(String upiId) {
        Account account = accountRepository.findByUpiId(upiId)
                .orElseThrow(() -> new PayportException("UPI ID not found", HttpStatus.NOT_FOUND));
        return account.getBalance();
    }

    public List<TransactionResponse> getTransactionHistory(String upiId) {
        if (!accountRepository.existsByUpiId(upiId)) {
            throw new PayportException("UPI ID not found", HttpStatus.NOT_FOUND);
        }
        return transactionRepository.findBySenderUpiOrReceiverUpi(upiId, upiId)
                .stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getSenderUpi(),
                        t.getReceiverUpi(),
                        t.getAmount(),
                        t.getTimestamp()))
                .toList();
    }

    public Account linkAccount(Long userId, String upiId, double balance) {
        if (accountRepository.existsByUpiId(upiId)) {
            throw new PayportException("UPI ID already taken", HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PayportException("User not found", HttpStatus.NOT_FOUND));

        Account account = new Account();
        account.setUser(user);
        account.setUpiId(upiId);
        account.setBalance(balance);

        return accountRepository.save(account);
    }
}