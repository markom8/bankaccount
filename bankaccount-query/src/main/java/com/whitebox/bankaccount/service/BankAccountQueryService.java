package com.whitebox.bankaccount.service;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.query.FindBankAccountQuery;
import lombok.AllArgsConstructor;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

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
}
