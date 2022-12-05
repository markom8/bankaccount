package com.whitebox.bankaccount.controller;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.controller.dto.BankDebitTransactionScheduledDTO;
import com.whitebox.bankaccount.event.BankAccountEvent;
import com.whitebox.bankaccount.exception.BankTransactionAmountException;
import com.whitebox.bankaccount.exception.BankTransactionExecutionDateTimeException;
import com.whitebox.bankaccount.service.BankAccountQueryService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/accounts")
@AllArgsConstructor
public class BankAccountQueryController {

    private final BankAccountQueryService bankAccountQueryService;

    /**
     * @param accountId
     * @return BankAccountEntity from the database
     */
    @GetMapping("/{accountId}")
    public CompletableFuture<BankAccountEntity> findById(@PathVariable("accountId") String accountId) {
        return this.bankAccountQueryService.findById(accountId);
    }

    /**
     * @param accountId
     * @param bankTransactionDTO debit amount must be positive and executionDateTime must be in the future and match the pattern "yyyy-MM-dd HH:mm:ss z"
     * @return message if the debit command will exceed the overdraft limit.
     * @throws BankTransactionAmountException
     * @throws BankTransactionExecutionDateTimeException
     */
    @PostMapping("/{accountId}")
    public CompletableFuture<String> testIfPendingBankAccountExceedsOverdraftLimit(@PathVariable(value = "accountId") String accountId,
                                                                                   @RequestBody BankDebitTransactionScheduledDTO bankTransactionDTO)
            throws BankTransactionAmountException, BankTransactionExecutionDateTimeException {

        if (bankTransactionDTO.getDebitAmount() == null || bankTransactionDTO.getDebitAmount().signum() < 0) {
            throw new BankTransactionAmountException(UUID.fromString(accountId), bankTransactionDTO.getDebitAmount());
        }
        if (bankTransactionDTO.getExecutionDateTime() == null || bankTransactionDTO.getExecutionDateTime().isBefore(ZonedDateTime.now())) {
            throw new BankTransactionExecutionDateTimeException(UUID.fromString(accountId), bankTransactionDTO.getExecutionDateTime());
        }
        return this.bankAccountQueryService.testPendingDebitOverdraftLimit(accountId, bankTransactionDTO.getDebitAmount(), bankTransactionDTO.getExecutionDateTime());
    }

    /**
     * @param accountId
     * @param scheduledFrom must be in the ISO.DATE_TIME standard for ex. 2018-10-31T01:30:00.000%2B01:00
     * @return list of events that are scheduled or have happened from the scheduledFrom date
     */
    @GetMapping("/{accountId}/events")
    public List<BankAccountEvent> listEventsForAccount(@PathVariable(value = "accountId") String accountId,
                                                       @RequestParam(value = "scheduledFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime scheduledFrom) {

        List<BankAccountEvent> bankAccountEvents = (List<BankAccountEvent>) this.bankAccountQueryService.listEventsForAccount(accountId);
        if (scheduledFrom != null) {
            ZonedDateTime finalExecutionDateTime = scheduledFrom;
            return bankAccountEvents
                    .stream()
                    .filter(event -> event.getExecutionDateTime().isAfter(finalExecutionDateTime))
                    .collect(Collectors.toList());
        }
        return bankAccountEvents;
    }

    /**
     * @return list of BankAccountEntity which have the balance less than zero
     */
    @GetMapping("all_in_the_red")
    public CompletableFuture<List<BankAccountEntity>> listAllAccountsInTheRed() {
        return this.bankAccountQueryService.findAllInTheRed();
    }


}
