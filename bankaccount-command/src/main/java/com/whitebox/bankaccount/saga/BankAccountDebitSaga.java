package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.event.debit.DebitBankAccountCompletedEvent;
import com.whitebox.bankaccount.event.debit.DebitBankAccountConstructEvent;
import com.whitebox.bankaccount.event.debit.DebitBankAccountEvent;
import com.whitebox.bankaccount.exception.BankAccountNotFoundException;
import com.whitebox.bankaccount.exception.BankAccountOverdraftLimitException;
import com.whitebox.bankaccount.service.BankAccountCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountDebitSaga {
    private final BankAccountCommandService bankAccountCommandService;
    private final BankAccountRepository repository;

    private Map<DebitBankAccountConstructEvent, ScheduleToken> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "scheduleToken")
    public void handle(final DebitBankAccountEvent event) throws BankAccountNotFoundException, BankAccountOverdraftLimitException {
        if (event.getScheduleToken() != null) {
            log.debug("Handling a Bank Account Debit command {}", event.getBankAccountId());
            Optional<BankAccountEntity> optionalBankAccount = this.repository.findById(event.getBankAccountId());

            if (optionalBankAccount.isPresent()) {
                BankAccountEntity bankAccount = optionalBankAccount.get();
                if (bankAccount.getBalance().subtract(event.getDebitAmount()).compareTo(new BigDecimal(0).subtract(bankAccount.getOverdraftLimit())) < 0) {
                    log.info("Bank Account Debit command exception. Overdraft limit exceeded !", event.getBankAccountId().toString());
                    bankAccountCommandService.debitMoneyToAccountCompleted(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "EXCEPTION - Overdraft limit exceeded !.");
                    throw new BankAccountOverdraftLimitException(event.getBankAccountId());
                }
                bankAccount.setBalance(bankAccount.getBalance().subtract(event.getDebitAmount()));
                this.repository.save(bankAccount);
                bankAccountCommandService.debitMoneyToAccountCompleted(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "SUCCESS");
            } else {
                log.info("Bank Account Debit command exception. Account {} not found!", event.getBankAccountId().toString());
                bankAccountCommandService.debitMoneyToAccountCompleted(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "EXCEPTION - Bank account not present exception.");
                throw new BankAccountNotFoundException(event.getBankAccountId());
            }
        }
    }

    @SagaEventHandler(associationProperty = "scheduleToken")
    public void handle(final DebitBankAccountCompletedEvent event) {
        endSaga();
    }

    @EndSaga
    private void endSaga() {
        log.info("Debit process completed.");
    }
}
