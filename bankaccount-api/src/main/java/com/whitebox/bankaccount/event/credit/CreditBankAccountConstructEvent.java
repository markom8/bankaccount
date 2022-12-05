package com.whitebox.bankaccount.event.credit;

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
public class CreditBankAccountConstructEvent extends BankAccountEvent {
    private final BigDecimal creditAmount;

    @Builder
    public CreditBankAccountConstructEvent(UUID bankAccountId,
                                           BigDecimal creditAmount,
                                           ZonedDateTime localDateTime,
                                           ScheduleToken scheduleToken) {
        super(bankAccountId, localDateTime, scheduleToken);
        this.creditAmount = creditAmount;
    }
}
