package com.dws.challenge.validators;

import com.dws.challenge.domain.Transfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class differentAccounts implements ConstraintValidator<DifferentAccountsValidator, Transfer> {

    @Override
    public void initialize(DifferentAccountsValidator constraint) {
    }

    @Override
    public boolean isValid(Transfer transfer, ConstraintValidatorContext context) {
        return !transfer.getSourceAccount().equals(transfer.getTargetAccount());
    }
}
