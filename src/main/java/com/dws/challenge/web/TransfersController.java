package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.BankAccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransfersController {

	private final AccountsService accountsService;

	private final TransactionsService transactionsService;

	private final NotificationService notificationService;

	@Autowired
	public TransfersController(AccountsService accountsService, TransactionsService transactionsService,
			NotificationService notificationService) {
		this.accountsService = accountsService;
		this.notificationService = notificationService;
		this.transactionsService = transactionsService;
	}

	@PostMapping()
	public ResponseEntity<Object> transferBalance(@RequestBody @Valid Transfer transferDetails) {
		log.info("Transferring {} Euros from account {} to account account {}", transferDetails.getAmount(),
				transferDetails.getSourceAccount(), transferDetails.getTargetAccount());

		//1. Perform the transfer - Assuming valid input data as it has been validated.
		try {
			this.transactionsService.transferMoney(transferDetails.getSourceAccount(),
					transferDetails.getTargetAccount(), transferDetails.getAmount());
		} catch (BankAccountNotFoundException | InsufficientBalanceException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		//2. If all went ok, notify through the available services about the transfer. I assume the notifications are
		// not mandatory and, if they fail, a 200 should still be returned. In other case, the previous transactions
		// should be rolled back and a 500 shold be returned.
		try {
			final Account sourceAccount = accountsService.getAccount(transferDetails.getSourceAccount());
			final Account targetAccount = accountsService.getAccount(transferDetails.getTargetAccount());
			notificationService.notifyAboutTransfer(sourceAccount, "You have sent " + transferDetails.getAmount()
					.toString() + "Euros to the account " + targetAccount.getAccountId());
			notificationService.notifyAboutTransfer(targetAccount, "You have received " + transferDetails.getAmount()
					.toString() + "Euros from the account " + sourceAccount.getAccountId());
		} catch (Exception e) {
			log.warn("there was an error notifying the transactions", e);
		}
		return ResponseEntity.ok().body(null);
	}

}
