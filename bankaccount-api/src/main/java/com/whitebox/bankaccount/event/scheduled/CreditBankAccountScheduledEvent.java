package com.whitebox.bankaccount.event.scheduled;

import com.whitebox.bankaccount.event.BankAccountEvent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.axonframework.eventhandling.scheduling.ScheduleToken;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class CreditBankAccountScheduledEvent extends BankAccountEvent implements Serializable {
    private final BigDecimal creditAmount;

    @Builder
    public CreditBankAccountScheduledEvent(UUID bankAccountId, BigDecimal creditAmount, ZonedDateTime executionDateTime, ScheduleToken token) {
        super(bankAccountId, executionDateTime, token);
        this.creditAmount = creditAmount;
    }
}
