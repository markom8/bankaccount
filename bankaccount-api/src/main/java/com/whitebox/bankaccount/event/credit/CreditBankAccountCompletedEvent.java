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
public class CreditBankAccountCompletedEvent extends BankAccountEvent {
    private final BigDecimal creditAmount;
    private String status;

    @Builder
    public CreditBankAccountCompletedEvent(UUID bankAccountId,
                                           BigDecimal creditAmount,
                                           String status,
                                           ZonedDateTime localDateTime,
                                           ScheduleToken scheduleToken) {
        super(bankAccountId, localDateTime, scheduleToken);
        this.creditAmount = creditAmount;
        this.status = status;
    }
}
