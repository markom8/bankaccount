package com.whitebox.bankaccount.exception;

import java.time.ZonedDateTime;
import java.util.UUID;

public class BankAccountScheduleException extends Exception {

    public BankAccountScheduleException(UUID bankAccountId, ZonedDateTime zonedDateTime) {
        super("Bank account with id = " + bankAccountId + " scheduled for " + zonedDateTime + "exception.");
    }
}
