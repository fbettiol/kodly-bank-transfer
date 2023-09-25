package com.dws.challenge.service;

import com.dws.challenge.exception.BankAccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionsService {
	private final AccountsRepository accountsRepository;

	@Autowired
	public TransactionsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	/**
	 * It transfers money from one account to another one.
	 *
	 * @param sourceAccountId - source account ID to get the money from
	 * @param targetAccountId - target account ID where to send the money
	 * @param amount          - amount of money to be sent
	 * @throws BankAccountNotFoundException if the source or target account do not exist
	 * @throws InsufficientBalanceException if the money in the source account is not enough for the transfer.
	 */
	public void transferMoney(final String sourceAccountId, final String targetAccountId, final BigDecimal amount) {

	}
}
