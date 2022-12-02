package com.whitebox.bankaccount.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenBankAccountCommand {

    @TargetAggregateIdentifier
    private UUID bankAccountId;
    private BigDecimal initialDeposite;
    private String user;
}
