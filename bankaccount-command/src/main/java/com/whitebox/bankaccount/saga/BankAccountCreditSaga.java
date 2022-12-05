package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.event.credit.CreditBankAccountCompletedEvent;
import com.whitebox.bankaccount.event.credit.CreditBankAccountConstructEvent;
import com.whitebox.bankaccount.event.credit.CreditBankAccountEvent;
import com.whitebox.bankaccount.exception.BankAccountNotFoundException;
import com.whitebox.bankaccount.service.BankAccountCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountCreditSaga {
    private final BankAccountCommandService bankAccountCommandService;
    private final BankAccountRepository repository;

    private Map<CreditBankAccountConstructEvent, ScheduleToken> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "scheduleToken")
    public void handle(final CreditBankAccountEvent event) throws BankAccountNotFoundException {
        if (event.getScheduleToken() != null) {
            log.debug("Handling a Bank Account Credit command {}", event.getBankAccountId());
            Optional<BankAccountEntity> optionalBankAccount = this.repository.findById(event.getBankAccountId());

            if (optionalBankAccount.isPresent()) {
                BankAccountEntity bankAccount = optionalBankAccount.get();
                bankAccount.setBalance(bankAccount.getBalance().add(event.getCreditAmount()));
                this.repository.save(bankAccount);
                bankAccountCommandService.creditMoneyToAccountCompleted(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "SUCCESS");
            } else {
                log.info("Bank Account Credit command exception. Account {} not found!", event.getBankAccountId().toString());
                bankAccountCommandService.creditMoneyToAccountCompleted(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "EXCEPTION - Bank account not present exception.");
                throw new BankAccountNotFoundException(event.getBankAccountId());
            }
        }
    }

    @SagaEventHandler(associationProperty = "scheduleToken")
    public void handle(final CreditBankAccountCompletedEvent event) {
        endSaga();
    }

    @EndSaga
    private void endSaga() {
        log.info("Credit process completed.");
    }
}
