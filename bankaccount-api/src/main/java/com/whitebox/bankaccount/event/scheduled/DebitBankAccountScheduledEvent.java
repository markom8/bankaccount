package com.whitebox.bankaccount.event.scheduled;

import com.whitebox.bankaccount.event.BankAccountEvent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.axonframework.eventhandling.scheduling.ScheduleToken;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebitBankAccountScheduledEvent extends BankAccountEvent {
    private final BigDecimal debitAmount;

    @Builder
    public DebitBankAccountScheduledEvent(UUID bankAccountId, BigDecimal debitAmount, ZonedDateTime executionDateTime, ScheduleToken token) {
        super(bankAccountId, executionDateTime, token);
        this.debitAmount = debitAmount;
    }
}
