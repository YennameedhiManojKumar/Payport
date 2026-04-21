package com.payport.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {

    @NotBlank(message = "Sender UPI ID is required")
    private String fromUpi;

    @NotBlank(message = "Receiver UPI ID is required")
    private String toUpi;

    @Min(value = 1, message = "Transfer amount must be at least 1")
    private double amount;
}