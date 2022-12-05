package com.whitebox.bankaccount.exception;

import java.time.ZonedDateTime;
import java.util.UUID;

public class BankTransactionExecutionDateTimeException extends Exception {

    public BankTransactionExecutionDateTimeException(UUID bankAccountId, ZonedDateTime zonedDateTime) {
        super("Bank account with id = " + bankAccountId + " has invalid zonedDateTime " + zonedDateTime);
    }
}
