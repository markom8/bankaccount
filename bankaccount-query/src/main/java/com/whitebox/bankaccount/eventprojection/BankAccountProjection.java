package com.whitebox.bankaccount.eventprojection;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.query.FindBankAccountQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountProjection {
    private final BankAccountRepository repository;
    private final QueryUpdateEmitter updateEmitter;

    @QueryHandler
    public BankAccountEntity handle(FindBankAccountQuery query) {
        log.debug("Handling FindBankAccountQuery query: {}", query);
        return this.repository.findById(query.getBankAccountId()).orElse(null);
    }
}
