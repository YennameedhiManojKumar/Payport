package com.payport.payment;

import com.payport.payment.exception.PayportException;
import com.payport.payment.model.Account;
import com.payport.payment.model.User;
import com.payport.payment.repository.AccountRepository;
import com.payport.payment.repository.TransactionRepository;
import com.payport.payment.repository.UserRepository;
import com.payport.payment.service.AccountService;
import com.payport.payment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayportSystemApplicationTests {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AccountService accountService;

	@InjectMocks
	private UserService userService;

	private Account senderAccount;
	private Account receiverAccount;
	private final PasswordEncoder realEncoder = new BCryptPasswordEncoder();
	private String encodedPin;

	@BeforeEach
	void setUp() {
		encodedPin = realEncoder.encode("1234");

		senderAccount = new Account();
		senderAccount.setUpiId("manoj@payport");
		senderAccount.setBalance(5000.0);
		senderAccount.setUpiPin(encodedPin);

		receiverAccount = new Account();
		receiverAccount.setUpiId("test@payport");
		receiverAccount.setBalance(1000.0);
		receiverAccount.setUpiPin(encodedPin);
	}

	// ─── AccountService: transfer ────────────────────────────────────────────

	@Test
	void transfer_shouldSucceed_whenValidRequest() {
		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(accountRepository.findByUpiId("test@payport"))
				.thenReturn(Optional.of(receiverAccount));
		when(passwordEncoder.matches("1234", encodedPin)).thenReturn(true);
		when(accountRepository.save(any())).thenReturn(null);
		when(transactionRepository.save(any())).thenReturn(null);

		assertDoesNotThrow(() -> accountService.transfer("manoj@payport", "test@payport", 500.0, "1234"));

		assertEquals(4500.0, senderAccount.getBalance());
		assertEquals(1500.0, receiverAccount.getBalance());
	}

	@Test
	void transfer_shouldFail_whenInsufficientBalance() {
		senderAccount.setBalance(100.0);

		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(accountRepository.findByUpiId("test@payport"))
				.thenReturn(Optional.of(receiverAccount));
		when(passwordEncoder.matches("1234", encodedPin)).thenReturn(true);

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.transfer("manoj@payport", "test@payport", 500.0, "1234"));

		assertEquals("Insufficient balance", ex.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void transfer_shouldFail_whenSameUpi() {
		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.transfer("manoj@payport", "manoj@payport", 500.0, "1234"));

		assertEquals("Sender and receiver cannot be the same", ex.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void transfer_shouldFail_whenZeroAmount() {
		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.transfer("manoj@payport", "test@payport", 0.0, "1234"));

		assertEquals("Transfer amount must be greater than zero", ex.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void transfer_shouldFail_whenWrongPin() {
		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(passwordEncoder.matches("9999", encodedPin)).thenReturn(false);

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.transfer("manoj@payport", "test@payport", 500.0, "9999"));

		assertEquals("Incorrect UPI PIN", ex.getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
	}

	@Test
	void transfer_shouldFail_whenSenderNotFound() {
		when(accountRepository.findByUpiId("unknown@payport"))
				.thenReturn(Optional.empty());

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.transfer("unknown@payport", "test@payport", 500.0, "1234"));

		assertEquals("Sender UPI ID not found", ex.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
	}

	// ─── AccountService: getBalance ──────────────────────────────────────────

	@Test
	void getBalance_shouldReturnBalance_whenCorrectPin() {
		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(passwordEncoder.matches("1234", encodedPin)).thenReturn(true);

		double balance = accountService.getBalance("manoj@payport", "1234");

		assertEquals(5000.0, balance);
	}

	@Test
	void getBalance_shouldFail_whenWrongPin() {
		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(passwordEncoder.matches("0000", encodedPin)).thenReturn(false);

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.getBalance("manoj@payport", "0000"));

		assertEquals("Incorrect UPI PIN", ex.getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
	}

	@Test
	void getBalance_shouldFail_whenUpiNotFound() {
		when(accountRepository.findByUpiId("ghost@payport"))
				.thenReturn(Optional.empty());

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.getBalance("ghost@payport", "1234"));

		assertEquals("UPI ID not found", ex.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
	}

	// ─── AccountService: setPin ──────────────────────────────────────────────

	@Test
	void setPin_shouldSucceed_whenPinNotSet() {
		senderAccount.setUpiPin(null);

		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));
		when(passwordEncoder.encode("1234")).thenReturn(encodedPin);
		when(accountRepository.save(any())).thenReturn(senderAccount);

		assertDoesNotThrow(() -> accountService.setPin("manoj@payport", "1234"));

		assertEquals(encodedPin, senderAccount.getUpiPin());
	}

	@Test
	void setPin_shouldFail_whenPinAlreadySet() {
		when(accountRepository.findByUpiId("manoj@payport"))
				.thenReturn(Optional.of(senderAccount));

		PayportException ex = assertThrows(PayportException.class,
				() -> accountService.setPin("manoj@payport", "1234"));

		assertEquals("PIN already set. Contact support to reset.", ex.getMessage());
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
	}

	// ─── UserService: registerUser ───────────────────────────────────────────

	@Test
	void registerUser_shouldSucceed_whenNewMobile() {
		User user = new User();
		user.setName("Manoj");
		user.setMobileNumber("9876543210");
		user.setPassword("manoj@123");

		when(userRepository.existsByMobileNumber("9876543210")).thenReturn(false);
		when(passwordEncoder.encode("manoj@123")).thenReturn("hashedPassword");
		when(userRepository.save(any())).thenReturn(user);

		User result = userService.registerUser(user);

		assertNotNull(result);
		verify(userRepository, times(1)).save(any());
	}

	@Test
	void registerUser_shouldFail_whenDuplicateMobile() {
		User user = new User();
		user.setName("Manoj");
		user.setMobileNumber("9876543210");
		user.setPassword("manoj@123");

		when(userRepository.existsByMobileNumber("9876543210")).thenReturn(true);

		PayportException ex = assertThrows(PayportException.class, () -> userService.registerUser(user));

		assertEquals("Mobile number already registered", ex.getMessage());
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
	}
}