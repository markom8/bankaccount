package com.whitebox.bankaccount.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BankAccountEvent {
    private final UUID bankAccountId;
    private final LocalDateTime executionDateTime;
    private TransactionStatus transactionStatus;
    private String scheduleToken;
}
