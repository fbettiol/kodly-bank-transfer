package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.BankAccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class TransactionsService {
	@Getter
	private final AccountsRepository accountsRepository;

	private final LocksHandler locksHandler;

	@Autowired
	public TransactionsService(AccountsRepository accountsRepository, LocksHandler atomicTransactionExecutor) {
		this.accountsRepository = accountsRepository;
		this.locksHandler = atomicTransactionExecutor;
	}

	/**
	 * It transfers money from one account to another one.
	 *
	 * @param sourceAccountId - source account to get the money from
	 * @param targetAccountId - target account where to send the money
	 * @param amount          - amount of money to be sent
	 * @throws BankAccountNotFoundException if the source or target account do not exist
	 * @throws InsufficientBalanceException if the money in the source account is not enough for the transfer.
	 */
	public void transferMoney(final String sourceAccountId, final String targetAccountId, final BigDecimal amount) {

		// To avoid deadlocks/race-conditions, the resources are accessed always on the same order. In that way,
		// one of the conditions that are required for a deadlock to happen is broken (making a transaction thread-safe).
		final List<String> sortedAccountsIds = Arrays.asList(sourceAccountId, targetAccountId);
		sortedAccountsIds.sort(String::compareTo);

		try {
			// Lock the accounts, to enable mutual exclusion
			locksHandler.lockAccount(sortedAccountsIds.get(0));
			locksHandler.lockAccount(sortedAccountsIds.get(1));

			// Extract the accounts and check that they exist. The checks are done after the locking to ensure that the
			// account was not deleted (possibly due to interliving).
			final Account sourceAccount = accountsRepository.getAccount(sourceAccountId);
			final Account targetAccount = accountsRepository.getAccount(targetAccountId);
			if (sourceAccount == null) {
				throw new BankAccountNotFoundException("The source account with id " + sourceAccountId + " was not found");
			} else if (targetAccount == null) {
				throw new BankAccountNotFoundException("The target account with id " + targetAccountId + " was not found");
			}

			// Check if the balance is enough in the source account.
			if (sourceAccount.getBalance().compareTo(amount) < 0) {
				throw new InsufficientBalanceException("The account does not have enough balance to transfer " + amount);
			}

			// Update the balance.
			// TODO: this works only for the in-memory implementation. For a DB, this should be changed or isolated behind an interface.
			sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
			targetAccount.setBalance(targetAccount.getBalance().add(amount));
		} finally {
			// Unlock the accounts.
			locksHandler.unlockAccount(sortedAccountsIds.get(1));
			locksHandler.unlockAccount(sortedAccountsIds.get(0));
		}
	}
}
