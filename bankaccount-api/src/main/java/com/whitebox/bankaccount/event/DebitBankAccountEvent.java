package com.whitebox.bankaccount.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebitBankAccountEvent extends BankAccountEvent {
    private final BigDecimal debitAmount;

    @Builder
    public DebitBankAccountEvent(UUID bankAccountId,
                                 BigDecimal debitAmount,
                                 LocalDateTime localDateTime,
                                 TransactionStatus transactionStatus,
                                 String scheduleToken) {
        super(bankAccountId, localDateTime, transactionStatus, scheduleToken);
        this.debitAmount = debitAmount;
    }
}
