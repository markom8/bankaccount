package com.whitebox.bankaccount.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreditBankAccountCompletedCommand {
    @TargetAggregateIdentifier
    private final UUID bankAccountId;
    private final BigDecimal creditAmount;
    private final ZonedDateTime executionDateTime;
    private ScheduleToken scheduleToken;
    private String status;
}
