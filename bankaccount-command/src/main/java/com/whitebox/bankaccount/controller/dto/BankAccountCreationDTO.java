package com.whitebox.bankaccount.controller.dto;

import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@ToString
public class BankAccountCreationDTO {
    private final BigDecimal initialBalance;
    private final String user;
    private final ZonedDateTime executionDate;
    private final BigDecimal overdraftLimit;
}
