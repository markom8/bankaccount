package com.whitebox.bankaccount.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class OpenBankAccountEvent extends BankAccountEvent {

    private final BigDecimal initialBalance;
    private final String user;

    @Builder
    public OpenBankAccountEvent(UUID bankAccountId, BigDecimal initialBalance, String user) {
        super(bankAccountId, null, TransactionStatus.PENDING, null);
        this.initialBalance = initialBalance;
        this.user = user;
    }
}
