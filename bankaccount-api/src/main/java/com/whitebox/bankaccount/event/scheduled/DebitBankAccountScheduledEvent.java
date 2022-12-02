package com.whitebox.bankaccount.event.scheduled;

import com.whitebox.bankaccount.event.BankAccountEvent;
import com.whitebox.bankaccount.event.TransactionStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebitBankAccountScheduledEvent extends BankAccountEvent {
    private final BigDecimal debitAmount;

    @Builder
    public DebitBankAccountScheduledEvent(UUID bankAccountId, BigDecimal debitAmount, LocalDateTime executionDateTime, TransactionStatus transactionStatus, String token) {
        super(bankAccountId, executionDateTime, transactionStatus, token);
        this.debitAmount = debitAmount;
    }
}
