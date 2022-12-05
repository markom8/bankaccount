package com.whitebox.bankaccount.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class OpenBankAccountEvent extends BankAccountEvent {

    private final BigDecimal initialBalance;
    private final BigDecimal overdraftLimit;
    private final String user;

    @Builder
    public OpenBankAccountEvent(UUID bankAccountId, BigDecimal initialBalance, BigDecimal overdraftLimit, String user) {
        super(bankAccountId, ZonedDateTime.now(), null);
        this.initialBalance = initialBalance;
        this.overdraftLimit = overdraftLimit;
        this.user = user;
    }
}
