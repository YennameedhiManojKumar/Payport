package com.payport.payment.controller;

import com.payport.payment.dto.request.SetPinRequest;
import com.payport.payment.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/set-pin")
    public ResponseEntity<Map<String, String>> setPin(@Valid @RequestBody SetPinRequest request) {
        accountService.setPin(request.getUpiId(), request.getPin());
        return ResponseEntity.ok(Map.of("message", "UPI PIN set successfully"));
    }
}