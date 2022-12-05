package com.whitebox.bankaccount.controller;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.controller.dto.BankAccountCreationDTO;
import com.whitebox.bankaccount.controller.dto.BankTransactionDTO;
import com.whitebox.bankaccount.exception.BankAccountAmountException;
import com.whitebox.bankaccount.exception.BankAccountCreationException;
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

    /**
     * @param creationDTO
     * @return
     * @throws BankAccountCreationException
     */
    @PostMapping
    @ResponseStatus(value = CREATED)
    public CompletableFuture<BankAccountEntity> createAccount(@RequestBody BankAccountCreationDTO creationDTO) throws BankAccountCreationException {
        return this.bankAccountCommandService.createAccount(creationDTO);
    }

    /**
     * @param accountId
     * @param bankTransactionDTO
     * @return
     * @throws BankAccountAmountException
     */
    @PutMapping(value = "/credit/{accountId}")
    public CompletableFuture<String> creditMoneyToAccount(@PathVariable(value = "accountId") String accountId,
                                                          @RequestBody BankTransactionDTO bankTransactionDTO) throws BankAccountAmountException {
        return this.bankAccountCommandService.creditMoneyToAccount(accountId, bankTransactionDTO);
    }

    /**
     * @param accountId
     * @param bankTransactionDTO
     * @return
     * @throws BankAccountAmountException
     */
    @PutMapping(value = "/debit/{accountId}")
    public CompletableFuture<String> debitMoneyFromAccount(@PathVariable(value = "accountId") String accountId,
                                                           @RequestBody BankTransactionDTO bankTransactionDTO) throws BankAccountAmountException {
        return this.bankAccountCommandService.debitMoneyFromAccount(accountId, bankTransactionDTO);
    }
}
