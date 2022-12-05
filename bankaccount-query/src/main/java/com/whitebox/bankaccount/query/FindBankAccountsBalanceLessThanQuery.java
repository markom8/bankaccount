package com.whitebox.bankaccount.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindBankAccountsBalanceLessThanQuery {
    private BigDecimal value;
}
