package com.whitebox.bankaccount.controller;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.controller.dto.BankAccountCreationDTO;
import com.whitebox.bankaccount.controller.dto.BankTransactionDTO;
import com.whitebox.bankaccount.service.BankAccountCommandService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(value = "/accounts")
@AllArgsConstructor
public class BankAccountCommandController {
    private final BankAccountCommandService bankAccountCommandService;

    @PostMapping
    @ResponseStatus(value = CREATED)
    public CompletableFuture<BankAccountEntity> createAccount(@RequestBody BankAccountCreationDTO creationDTO) {
        return this.bankAccountCommandService.createAccount(creationDTO);
    }

    @PutMapping(value = "/credit/{accountId}")
    public CompletableFuture<String> creditMoneyToAccount(@PathVariable(value = "accountId") String accountId,
                                                          @RequestBody BankTransactionDTO bankTransactionDTO) {
        return this.bankAccountCommandService.creditMoneyToAccount(accountId, bankTransactionDTO);
    }

    @PutMapping(value = "/debit/{accountId}")
    public CompletableFuture<String> debitMoneyFromAccount(@PathVariable(value = "accountId") String accountId,
                                                           @RequestBody BankTransactionDTO bankTransactionDTO) {
        return this.bankAccountCommandService.debitMoneyFromAccount(accountId, bankTransactionDTO);
    }
}
