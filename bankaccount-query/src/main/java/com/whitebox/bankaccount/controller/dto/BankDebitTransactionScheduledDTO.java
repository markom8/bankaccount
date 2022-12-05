package com.whitebox.bankaccount.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class BankDebitTransactionScheduledDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private ZonedDateTime executionDateTime;
    private BigDecimal debitAmount;

}
