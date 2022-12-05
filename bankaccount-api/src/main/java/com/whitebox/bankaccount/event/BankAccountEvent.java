package com.whitebox.bankaccount.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.axonframework.eventhandling.scheduling.ScheduleToken;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BankAccountEvent {
    private final UUID bankAccountId;
    @EqualsAndHashCode.Exclude
    private final ZonedDateTime executionDateTime;
    private ScheduleToken scheduleToken;
}
