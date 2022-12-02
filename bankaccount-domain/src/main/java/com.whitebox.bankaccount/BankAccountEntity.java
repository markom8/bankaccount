package com.whitebox.bankaccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bank_account")
public class BankAccountEntity {
    @Id
    @Type(type = "uuid-char")
    private UUID bankAccountId;
    private String user;
    private BigDecimal balance;
}
