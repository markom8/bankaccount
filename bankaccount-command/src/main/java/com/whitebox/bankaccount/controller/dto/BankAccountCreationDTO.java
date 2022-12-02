package com.whitebox.bankaccount.controller.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class BankAccountCreationDTO {
    private final BigDecimal initialBalance;
    private final String user;
    private final LocalDateTime executionDate;
}
