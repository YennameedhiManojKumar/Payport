package com.payport.payment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderUpi;
    private String receiverUpi;
    private double amount;
    private LocalDateTime timestamp;

    // Constructors
    public Transaction() {}

    public Transaction(String senderUpi, String receiverUpi, double amount, LocalDateTime timestamp) {
        this.senderUpi = senderUpi;
        this.receiverUpi = receiverUpi;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getSenderUpi() { return senderUpi; }
    public void setSenderUpi(String senderUpi) { this.senderUpi = senderUpi; }

    public String getReceiverUpi() { return receiverUpi; }
    public void setReceiverUpi(String receiverUpi) { this.receiverUpi = receiverUpi; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
