package com.payport.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "UPI PIN is required")
    @Pattern(regexp = "^[0-9]{4,6}$", message = "PIN must be 4 to 6 digits")
    private String pin;
}