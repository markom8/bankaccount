package com.whitebox.bankaccount.service;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.command.CreditBankAccountCommand;
import com.whitebox.bankaccount.command.DebitBankAccountCommand;
import com.whitebox.bankaccount.command.DeleteBankAccountCommand;
import com.whitebox.bankaccount.command.OpenBankAccountCommand;
import com.whitebox.bankaccount.controller.dto.BankAccountCreationDTO;
import com.whitebox.bankaccount.controller.dto.BankTransactionDTO;
import com.whitebox.bankaccount.event.TransactionStatus;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.whitebox.bankaccount.ServiceUtils.formatUuid;


@Service
@AllArgsConstructor
public class BankAccountCommandService {
    private final CommandGateway commandGateway;

    public CompletableFuture<BankAccountEntity> createAccount(BankAccountCreationDTO creationDTO) {
        return this.commandGateway.send(new OpenBankAccountCommand(
                UUID.randomUUID(),
                creationDTO.getInitialBalance(),
                creationDTO.getUser()
        ));
    }

    public CompletableFuture<String> creditMoneyToAccount(String accountId,
                                                          BankTransactionDTO bankTransactionDTO) {
        return this.commandGateway.send(new CreditBankAccountCommand(
                UUID.fromString(accountId),
                bankTransactionDTO.getAmount(),
                bankTransactionDTO.getExecutionDateTime(),
                null,
                null
        ));
    }

    public CompletableFuture<String> debitMoneyFromAccount(String accountId,
                                                           BankTransactionDTO bankTransactionDTO) {
        return this.commandGateway.send(new DebitBankAccountCommand(
                formatUuid(accountId),
                bankTransactionDTO.getAmount(),
                bankTransactionDTO.getExecutionDateTime(),
                null,
                null
        ));
    }

    public void creditMoneyToAccountTransactionStatus(UUID accountId,
                                                      BigDecimal amount,
                                                      LocalDateTime localDateTime,
                                                      TransactionStatus transactionStatus,
                                                      String scheduleToken) {
        this.commandGateway.send(new CreditBankAccountCommand(
                accountId,
                amount,
                localDateTime,
                transactionStatus,
                scheduleToken
        ));
    }

    public void debitMoneyToAccountTransactionStatus(UUID accountId,
                                                     BigDecimal amount,
                                                     LocalDateTime localDateTime,
                                                     TransactionStatus transactionStatus,
                                                     String scheduleToken) {
        this.commandGateway.send(new DebitBankAccountCommand(
                accountId,
                amount,
                localDateTime,
                transactionStatus,
                scheduleToken
        ));
    }

    public void markEventDeleted(UUID accountId) {
        this.commandGateway.send(new DeleteBankAccountCommand(accountId));
    }
}
