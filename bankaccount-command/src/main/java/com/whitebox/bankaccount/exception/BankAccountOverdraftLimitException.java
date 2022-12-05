package com.whitebox.bankaccount.exception;

import java.util.UUID;

public class BankAccountOverdraftLimitException extends Exception {

    public BankAccountOverdraftLimitException(UUID bankAccountId) {
        super("Bank account with id = " + bankAccountId + " has reached overdraft limit. Debit not possible.");
    }
}
