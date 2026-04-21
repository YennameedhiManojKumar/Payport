package com.payport.payment.repository;

import com.payport.payment.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUpiId(String upiId);

    boolean existsByUpiId(String upiId);

    Optional<Account> findByUserMobileNumber(String mobileNumber);
}