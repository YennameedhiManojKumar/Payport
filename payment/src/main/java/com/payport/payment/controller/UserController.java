package com.payport.payment.controller;

import com.payport.payment.dto.RegisterLinkRequest;
import com.payport.payment.model.Account;
import com.payport.payment.model.User;
import com.payport.payment.service.UserService;
import com.payport.payment.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    // Existing endpoint
    @PostMapping("/link-account")
    public Account linkAccount(@RequestParam Long userId,
            @RequestParam String upiId,
            @RequestParam double balance) {
        return userService.linkAccount(userId, upiId, balance);
    }

    // New combined register + link endpoint
    @PostMapping("/registerAndLink")
    public ResponseEntity<?> registerAndLink(@RequestBody RegisterLinkRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber());
        user = userService.registerUser(user);

        accountService.linkAccount(user.getId(), request.getUpiId(), request.getBalance());
        return ResponseEntity.ok(user);
    }
}
