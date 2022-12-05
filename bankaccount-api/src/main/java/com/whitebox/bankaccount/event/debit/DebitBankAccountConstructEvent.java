package com.whitebox.bankaccount.event.debit;

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
public class DebitBankAccountConstructEvent extends BankAccountEvent {
    private final BigDecimal debitAmount;

    @Builder
    public DebitBankAccountConstructEvent(UUID bankAccountId,
                                          BigDecimal debitAmount,
                                          ZonedDateTime localDateTime,
                                          ScheduleToken scheduleToken) {
        super(bankAccountId, localDateTime, scheduleToken);
        this.debitAmount = debitAmount;
    }
}
