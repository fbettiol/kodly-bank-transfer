package com.dws.challenge.domain;

import com.dws.challenge.validators.DifferentAccountsValidator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@DifferentAccountsValidator
public class Transfer {
	@NotNull
	@NotEmpty
	private String sourceAccount;

	@NotNull
	@NotEmpty
	private String targetAccount;

	@NotNull
	@Positive(message = "The amount to transfer hast to be grater than zero.")
	private BigDecimal amount;

	@JsonCreator
	public Transfer(@JsonProperty("sourceAccount") String sourceAccount,
			@JsonProperty("targetAccount") String targetAccount, @JsonProperty("amount") BigDecimal amount) {
		this.sourceAccount = sourceAccount;
		this.targetAccount = targetAccount;
		this.amount = amount;
	}
}
