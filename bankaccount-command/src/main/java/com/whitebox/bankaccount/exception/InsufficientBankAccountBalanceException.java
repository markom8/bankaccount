package com.whitebox.bankaccount.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBankAccountBalanceException extends Exception {

    public InsufficientBankAccountBalanceException(UUID bankAccountId, BigDecimal debitAmount) {
        super("Issuficcient ballance: " + debitAmount + "for the bank account " + bankAccountId);
    }
}
