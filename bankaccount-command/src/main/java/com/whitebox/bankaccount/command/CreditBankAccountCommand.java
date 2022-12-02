package com.whitebox.bankaccount.command;

import com.whitebox.bankaccount.event.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreditBankAccountCommand {
    @TargetAggregateIdentifier
    private final UUID bankAccountId;
    private final BigDecimal creditAmount;
    private final LocalDateTime executionDateTime;
    private TransactionStatus transactionStatus;
    private String scheduleToken;
}
