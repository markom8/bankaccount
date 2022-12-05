package com.whitebox.bankaccount.service;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.event.BankAccountEvent;
import com.whitebox.bankaccount.event.scheduled.CreditBankAccountScheduledEvent;
import com.whitebox.bankaccount.event.scheduled.DebitBankAccountScheduledEvent;
import com.whitebox.bankaccount.query.FindBankAccountQuery;
import com.whitebox.bankaccount.query.FindBankAccountsBalanceLessThanQuery;
import lombok.AllArgsConstructor;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BankAccountQueryService {
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    public CompletableFuture<BankAccountEntity> findById(String bankAccountId) {
        return this.queryGateway.query(
                new FindBankAccountQuery(UUID.fromString(bankAccountId)),
                ResponseTypes.instanceOf(BankAccountEntity.class)
        );
    }

    public List<?> listEventsForAccount(String bankAccountId) {
        return this.eventStore
                .readEvents(UUID.fromString(bankAccountId).toString())
                .asStream()
                .map(Message::getPayload)
                .collect(Collectors.toList());
    }

    public CompletableFuture<List<BankAccountEntity>> findAllInTheRed() {
        return this.queryGateway.query(
                new FindBankAccountsBalanceLessThanQuery(new BigDecimal(0)),
                ResponseTypes.multipleInstancesOf(BankAccountEntity.class)
        );
    }

    public CompletableFuture<String> testPendingDebitOverdraftLimit(String bankAccountId, BigDecimal debitAmount, ZonedDateTime debitExecutionDateTime) {
        CompletableFuture<BankAccountEntity> bankAccountEntityCompletableFuture = findById(bankAccountId);
        return bankAccountEntityCompletableFuture.thenApply(bankAccountEntity -> {
            BigDecimal summarize = bankAccountEntity.getBalance();
            List<BankAccountEvent> bankAccountEvents = (List<BankAccountEvent>) listEventsForAccount(bankAccountId);
            bankAccountEvents = bankAccountEvents.stream()
                    .filter(event -> event.getExecutionDateTime().isAfter(ZonedDateTime.now()) && event.getExecutionDateTime().isBefore(debitExecutionDateTime))
                    .collect(Collectors.toList());
            for (BankAccountEvent bankAccountEvent : bankAccountEvents) {
                if (bankAccountEvent.getClass().equals(CreditBankAccountScheduledEvent.class)) {
                    CreditBankAccountScheduledEvent creditBankAccountScheduledEvent = (CreditBankAccountScheduledEvent) bankAccountEvent;
                    summarize = summarize.add(creditBankAccountScheduledEvent.getCreditAmount());
                } else if (bankAccountEvent.getClass().equals(DebitBankAccountScheduledEvent.class)) {
                    DebitBankAccountScheduledEvent debitBankAccountScheduledEvent = (DebitBankAccountScheduledEvent) bankAccountEvent;
                    summarize = summarize.subtract(debitBankAccountScheduledEvent.getDebitAmount());
                }
            }
            summarize = summarize.subtract(debitAmount);
            if (summarize.compareTo(new BigDecimal(0).subtract(bankAccountEntity.getOverdraftLimit())) < 0) {
                return "The overdraft limit will be exceeded by the debit command.";
            }
            return "The overdraft limit will not be exceeded by the debit command.";
        });
    }
}
