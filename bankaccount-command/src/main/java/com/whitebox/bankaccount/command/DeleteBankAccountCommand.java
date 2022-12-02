package com.whitebox.bankaccount.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteBankAccountCommand {
    @TargetAggregateIdentifier
    private final UUID bankAccountId;
}
