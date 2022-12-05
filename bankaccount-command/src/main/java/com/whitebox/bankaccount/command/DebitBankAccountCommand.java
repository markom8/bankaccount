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
public class DebitBankAccountCommand {
    @TargetAggregateIdentifier
    private final UUID bankAccountId;
    private final BigDecimal debitAmount;
    private final ZonedDateTime executionDateTime;
    private ScheduleToken scheduleToken;
}
