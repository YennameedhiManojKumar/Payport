package com.payport.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home.html";
    }

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    @GetMapping("/history")
    public String history() {
        return "history.html";
    }

    @GetMapping("/balance")
    public String balance() {
        return "balance.html";
    }

    @GetMapping("/transfer")
    public String transfer() {
        return "transfer.html";
    }
}