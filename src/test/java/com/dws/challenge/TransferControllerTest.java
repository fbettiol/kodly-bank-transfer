package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transfer;
import com.dws.challenge.exception.BankAccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransactionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class TransferControllerTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private AccountsService accountsService;

	@MockBean
	private TransactionsService transactionsService;

	@MockBean
	private NotificationService notificationService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	void transferEmptySenderTest() throws Exception {

		//Given
		final Transfer transferRequest = new Transfer("", "ac2", new BigDecimal(12));

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest());
	}

	@Test
	void transferNullReceiverTest() throws Exception {

		//Given
		final Transfer transferRequest = new Transfer("ac1", null, new BigDecimal(12));

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest());
	}

	@Test
	void transferBetweenSameAccountsTest() throws Exception {

		//Given
		final Transfer transferRequest = new Transfer("ac1", "ac1", new BigDecimal(12));

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest());
	}

	@Test
	void transferNegativeAmountTest() throws Exception {

		//Given
		final Transfer transferRequest = new Transfer("ac1", "ac2", new BigDecimal(-12));

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest());

	}

	@Test
	void transferUnexistentAccountTest() throws Exception {
		//Given
		final BigDecimal amount = new BigDecimal(100);
		final Transfer transferRequest = new Transfer("ac1", "ac2", amount);
		doThrow(new BankAccountNotFoundException("ac1 not found")).when(transactionsService)
				.transferMoney("ac1", "ac2", amount);

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest()).andExpect(content().string("ac1 not found"));
	}

	@Test
	void transferInsufficientBalanceTest() throws Exception {
		//Given
		final BigDecimal amount = new BigDecimal(100);
		final Transfer transferRequest = new Transfer("ac1", "ac2", amount);
		doThrow(new InsufficientBalanceException("Insufficient balance")).when(transactionsService)
				.transferMoney("ac1", "ac2", amount);

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isBadRequest()).andExpect(content().string("Insufficient balance"));
	}

	@Test
	void unexpectedErrorTest() throws Exception {
		//Given
		final BigDecimal amount = new BigDecimal(100);
		final Transfer transferRequest = new Transfer("ac1", "ac2", amount);
		doThrow(new RuntimeException("Unexpected error occurred")).when(transactionsService)
				.transferMoney("ac1", "ac2", amount);

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isInternalServerError()).andExpect(content().string("Unexpected error occurred"));

	}

	@Test
	void transferOkTest() throws Exception {
		//Given
		final Account account1 = new Account("ac1", new BigDecimal(100));
		final Account account2 = new Account("ac2", new BigDecimal(100));
		final BigDecimal amount = new BigDecimal(100);
		final Transfer transferRequest = new Transfer(account1.getAccountId(), account2.getAccountId(), amount);
		when(accountsService.getAccount(account1.getAccountId())).thenReturn(account1);
		when(accountsService.getAccount(account2.getAccountId())).thenReturn(account2);
		doNothing().when(transactionsService).transferMoney(account1.getAccountId(), account2.getAccountId(), amount);

		//When
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transferRequest)))

        //Then
        .andExpect(status().isOk());
		verify(notificationService, times(1)).notifyAboutTransfer(account1,
				"You have sent 100Euros to the account ac2");
		verify(notificationService, times(1)).notifyAboutTransfer(account2,
				"You have received 100Euros from the account ac1");

	}
}
