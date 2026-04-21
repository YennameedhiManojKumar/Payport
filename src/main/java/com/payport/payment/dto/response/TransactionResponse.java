package com.payport.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String senderUpi;
    private String receiverUpi;
    private double amount;
    private LocalDateTime timestamp;
}