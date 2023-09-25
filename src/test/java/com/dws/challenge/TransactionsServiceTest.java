package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.BankAccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.LocksHandler;
import com.dws.challenge.service.TransactionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransactionsServiceTest {

	@Autowired
	private TransactionsService uut; // Unit under test.

	@MockBean
	private AccountsRepository accountsRepository;

	@MockBean
	private LocksHandler locksHandler;

	@Test
	void transferMoney_failsOnNonexistentSourceAccount() {
		//Given
		final Account ac2 = mock(Account.class);
		when(accountsRepository.getAccount("ac1")).thenReturn(null);
		when(accountsRepository.getAccount("ac2")).thenReturn(ac2);

		//When - then
		assertThrows(BankAccountNotFoundException.class,
				() -> this.uut.transferMoney("ac1", "ac2", new BigDecimal(100)));

	}

	@Test
	void transferMoney_failsOnNonexistentTargetAccount() {
		//Given
		final Account ac1 = mock(Account.class);
		when(accountsRepository.getAccount("ac1")).thenReturn(ac1);
		when(accountsRepository.getAccount("ac2")).thenReturn(null);

		//When - then
		assertThrows(BankAccountNotFoundException.class,
				() -> this.uut.transferMoney("ac1", "ac2", new BigDecimal(100)));

	}

	@Test
	void transferMoney_failsOnNotEnoughBalance() {
		//Given
		final Account ac1 = new Account("ac1", new BigDecimal(100));
		final Account ac2 = new Account("ac2", new BigDecimal(100));
		when(accountsRepository.getAccount("ac1")).thenReturn(ac1);
		when(accountsRepository.getAccount("ac2")).thenReturn(ac2);

		//When - Then
		assertThrows(InsufficientBalanceException.class,
				() -> this.uut.transferMoney("ac1", "ac2", new BigDecimal(150)));

	}

	@Test
	void transferMoney_ok() {
		//Given
		final Account ac1 = new Account("ac1", new BigDecimal(100));
		final Account ac2 = new Account("ac2", new BigDecimal(100));
		when(accountsRepository.getAccount("ac1")).thenReturn(ac1);
		when(accountsRepository.getAccount("ac2")).thenReturn(ac2);

		//When
		this.uut.transferMoney("ac2", "ac1", new BigDecimal(50));

		//Then
		assertEquals(new BigDecimal(150), ac1.getBalance());
		assertEquals(new BigDecimal(50), ac2.getBalance());

		//Check the locks are acquired in the right order
		final InOrder orderVerifier = Mockito.inOrder(locksHandler);
		orderVerifier.verify(locksHandler).lockAccount("ac1");
		orderVerifier.verify(locksHandler).lockAccount("ac2");
		orderVerifier.verify(locksHandler).unlockAccount("ac2");
		orderVerifier.verify(locksHandler).unlockAccount("ac1");
	}

}
