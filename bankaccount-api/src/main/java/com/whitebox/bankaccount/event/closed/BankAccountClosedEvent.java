package com.whitebox.bankaccount.event.closed;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BankAccountClosedEvent {
    private final UUID bankAccountId;
}
