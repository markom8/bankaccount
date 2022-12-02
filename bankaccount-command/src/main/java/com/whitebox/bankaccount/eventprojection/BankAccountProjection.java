package com.whitebox.bankaccount.eventprojection;

import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.event.OpenBankAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountProjection {
    private final BankAccountRepository repository;

    @EventHandler
    public void on(OpenBankAccountEvent event) {
        log.debug("Bank Account creation command {}", event.getBankAccountId());
        BankAccountEntity bankAccount = new BankAccountEntity(
                event.getBankAccountId(),
                event.getUser(),
                event.getInitialBalance()
        );
        this.repository.save(bankAccount);
    }
}
