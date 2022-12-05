package com.whitebox.bankaccount.eventprojection;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.query.FindBankAccountQuery;
import com.whitebox.bankaccount.query.FindBankAccountsBalanceLessThanQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @QueryHandler
    public List<BankAccountEntity> handle(FindBankAccountsBalanceLessThanQuery query) {
        log.debug("Handling FindBankAccountsQuery query: {}", query);
        return this.repository.findByBalanceLessThan(query.getValue());
    }


}
