package com.whitebox.bankaccount.service;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.command.*;
import com.whitebox.bankaccount.controller.dto.BankAccountCreationDTO;
import com.whitebox.bankaccount.controller.dto.BankTransactionDTO;
import com.whitebox.bankaccount.exception.BankAccountAmountException;
import com.whitebox.bankaccount.exception.BankAccountCreationException;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventhandling.scheduling.java.SimpleScheduleToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.whitebox.bankaccount.ServiceUtils.formatUuid;


@Service
@AllArgsConstructor
public class BankAccountCommandService {
    private final CommandGateway commandGateway;

    public CompletableFuture<BankAccountEntity> createAccount(BankAccountCreationDTO creationDTO) throws BankAccountCreationException {
        if (creationDTO.getInitialBalance() == null || creationDTO.getOverdraftLimit() == null || creationDTO.getOverdraftLimit().signum() < 0 || creationDTO.getInitialBalance().signum() < 0 || creationDTO.getUser() == null) {
            throw new BankAccountCreationException(creationDTO);
        }
        return this.commandGateway.send(new OpenBankAccountCommand(
                UUID.randomUUID(),
                creationDTO.getInitialBalance(),
                creationDTO.getUser(),
                creationDTO.getOverdraftLimit()
        ));
    }

    /**
     * @param accountId
     * @param bankTransactionDTO
     * @return
     * @throws BankAccountAmountException
     */
    public CompletableFuture<String> creditMoneyToAccount(String accountId,
                                                          BankTransactionDTO bankTransactionDTO) throws BankAccountAmountException {

        if (bankTransactionDTO.getAmount() == null || bankTransactionDTO.getAmount().signum() < 0) {
            throw new BankAccountAmountException(UUID.fromString(accountId), bankTransactionDTO.getAmount());
        }

        // if executionDateTime is null the credit event is executed now, in the other case it is scheduled.
        if (bankTransactionDTO.getExecutionDateTime() == null) {
            return creditMoneyToAccountEvent(UUID.fromString(accountId),
                    bankTransactionDTO.getAmount(),
                    ZonedDateTime.now(),
                    new SimpleScheduleToken("")
            );
        }
        return this.commandGateway.send(new CreditBankAccountCommand(
                UUID.fromString(accountId),
                bankTransactionDTO.getAmount(),
                bankTransactionDTO.getExecutionDateTime(),
                null
        ));
    }

    /**
     * @param accountId
     * @param bankTransactionDTO
     * @return
     * @throws BankAccountAmountException
     */
    public CompletableFuture<String> debitMoneyFromAccount(String accountId,
                                                           BankTransactionDTO bankTransactionDTO) throws BankAccountAmountException {

        if (bankTransactionDTO.getAmount() == null || bankTransactionDTO.getAmount().signum() < 0) {
            throw new BankAccountAmountException(UUID.fromString(accountId), bankTransactionDTO.getAmount());
        }

        // if executionDateTime is null the debit command is executed now, in the other case it is scheduled.
        if (bankTransactionDTO.getExecutionDateTime() == null) {
            return debitMoneyToAccountEvent(UUID.fromString(accountId),
                    bankTransactionDTO.getAmount(),
                    ZonedDateTime.now(),
                    new SimpleScheduleToken("")
            );
        }
        return this.commandGateway.send(new DebitBankAccountCommand(
                formatUuid(accountId),
                bankTransactionDTO.getAmount(),
                bankTransactionDTO.getExecutionDateTime(),
                null
        ));
    }

    public CompletableFuture<String> creditMoneyToAccountEvent(UUID accountId,
                                                               BigDecimal amount,
                                                               ZonedDateTime localDateTime,
                                                               ScheduleToken scheduleToken) {
        return this.commandGateway.send(new CreditBankAccountCommand(
                accountId,
                amount,
                localDateTime,
                scheduleToken
        ));
    }

    public CompletableFuture<String> creditMoneyToAccountCompleted(UUID accountId,
                                                                   BigDecimal amount,
                                                                   ZonedDateTime localDateTime,
                                                                   ScheduleToken scheduleToken,
                                                                   String status) {
        return this.commandGateway.send(new CreditBankAccountCompletedCommand(
                accountId,
                amount,
                localDateTime,
                scheduleToken,
                status
        ));
    }

    public CompletableFuture<String> debitMoneyToAccountEvent(UUID accountId,
                                                              BigDecimal amount,
                                                              ZonedDateTime localDateTime,
                                                              ScheduleToken scheduleToken) {
        return this.commandGateway.send(new DebitBankAccountCommand(
                accountId,
                amount,
                localDateTime,
                scheduleToken
        ));
    }

    public CompletableFuture<String> debitMoneyToAccountCompleted(UUID accountId,
                                                                  BigDecimal amount,
                                                                  ZonedDateTime localDateTime,
                                                                  ScheduleToken scheduleToken,
                                                                  String status) {
        return this.commandGateway.send(new DebitBankAccountCompletedCommand(
                accountId,
                amount,
                localDateTime,
                scheduleToken,
                status
        ));
    }

    public CompletableFuture<String> markEventDeleted(UUID accountId) {
        return this.commandGateway.send(new DeleteBankAccountCommand(accountId));
    }
}
