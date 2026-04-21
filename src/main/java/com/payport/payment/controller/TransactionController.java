package com.payport.payment.controller;

import com.payport.payment.dto.request.TransferRequest;
import com.payport.payment.model.Transaction;
import com.payport.payment.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final AccountService accountService;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(request.getFromUpi(), request.getToUpi(), request.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Double>> checkBalance(@RequestParam String upiId) {
        double balance = accountService.getBalance(upiId);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(@RequestParam String upiId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(upiId));
    }
}