package com.payport.payment.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payport.payment.model.Transaction;
import com.payport.payment.service.AccountService;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String fromUpi,
            @RequestParam String toUpi,
            @RequestParam double amount) {
        return accountService.transfer(fromUpi, toUpi, amount);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> checkBalance(@RequestParam String upiId) {
        try {
            double balance = accountService.getBalance(upiId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // ✅ Transaction history endpoint
    @GetMapping("/history")
    public List<Transaction> getHistory(@RequestParam String upiId) {
        return accountService.getTransactionHistory(upiId);
    }
}
