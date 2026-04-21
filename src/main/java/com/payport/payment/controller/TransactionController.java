package com.payport.payment.controller;

import com.payport.payment.dto.request.BalanceRequest;
import com.payport.payment.dto.request.TransferRequest;
import com.payport.payment.dto.response.BalanceResponse;
import com.payport.payment.dto.response.TransactionResponse;
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
        accountService.transfer(
                request.getFromUpi(),
                request.getToUpi(),
                request.getAmount(),
                request.getPin());
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    @PostMapping("/balance")
    public ResponseEntity<BalanceResponse> checkBalance(@Valid @RequestBody BalanceRequest request) {
        double balance = accountService.getBalance(request.getUpiId(), request.getPin());
        return ResponseEntity.ok(new BalanceResponse(request.getUpiId(), balance));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getHistory(@RequestParam String upiId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(upiId));
    }
}