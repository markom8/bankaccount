package com.whitebox.bankaccount.event.scheduled;

import com.whitebox.bankaccount.event.BankAccountEvent;
import com.whitebox.bankaccount.event.TransactionStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class CreditBankAccountScheduledEvent extends BankAccountEvent implements Serializable {
    private final BigDecimal creditAmount;

    @Builder
    public CreditBankAccountScheduledEvent(UUID bankAccountId, BigDecimal creditAmount, LocalDateTime executionDateTime, TransactionStatus transactionStatus, String token) {
        super(bankAccountId, executionDateTime, transactionStatus, token);
        this.creditAmount = creditAmount;
    }
}
