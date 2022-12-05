package com.whitebox.bankaccount.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountAmountException extends Exception {

    public BankAccountAmountException(UUID bankAccountId, BigDecimal amount) {
        super("Bank account with id = " + bankAccountId + " has invalid amount " + amount);
    }
}
