package com.whitebox.bankaccount.exception;

import java.util.UUID;

public class BankAccountNotFoundException extends Exception {

    public BankAccountNotFoundException(UUID bankAccountId) {
        super("Bank account with id = " + bankAccountId + " not found.");
    }
}
