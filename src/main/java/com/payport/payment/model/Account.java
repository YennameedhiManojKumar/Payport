package com.payport.payment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String upiId;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
