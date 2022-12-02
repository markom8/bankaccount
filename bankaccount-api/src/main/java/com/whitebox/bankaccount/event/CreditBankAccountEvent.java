package com.whitebox.bankaccount.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class CreditBankAccountEvent extends BankAccountEvent {
    private final BigDecimal creditAmount;

    @Builder
    public CreditBankAccountEvent(UUID bankAccountId,
                                  BigDecimal creditAmount,
                                  LocalDateTime localDateTime,
                                  TransactionStatus transactionStatus,
                                  String scheduleToken) {
        super(bankAccountId, localDateTime, transactionStatus, scheduleToken);
        this.creditAmount = creditAmount;
    }
}
