package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.event.CreditBankAccountEvent;
import com.whitebox.bankaccount.event.TransactionStatus;
import com.whitebox.bankaccount.event.scheduled.CreditBankAccountScheduledEvent;
import com.whitebox.bankaccount.exception.BankAccountNotFoundException;
import com.whitebox.bankaccount.service.BankAccountCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountCreditSaga {
    private final EventScheduler eventScheduler;
    private final BankAccountCommandService bankAccountCommandService;
    private final BankAccountRepository repository;
    private final EventStore eventStore;

    private Map<CreditBankAccountEvent, String> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "bankAccountId")
    public void on(CreditBankAccountScheduledEvent event) {
        log.debug("Handling a Scheduled Bank Account Credit command {}", event.getBankAccountId().toString());
        if (event.getExecutionDateTime().isAfter(LocalDateTime.now())) {
            CreditBankAccountEvent creditBankAccountEvent = new CreditBankAccountEvent(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), TransactionStatus.PENDING, null);
            ScheduleToken scheduleToken = eventScheduler.schedule(event.getExecutionDateTime().atZone(ZoneId.systemDefault()).toInstant(), creditBankAccountEvent);
            schedule.put(creditBankAccountEvent, scheduleToken.toString());
        }
    }

    @SagaEventHandler(associationProperty = "bankAccountId")
    public void handle(final CreditBankAccountEvent event) throws BankAccountNotFoundException {
        if (event.getScheduleToken() != null) {
            log.debug("Handling a Bank Account Credit command {}", event.getBankAccountId());
            Optional<BankAccountEntity> optionalBankAccount = this.repository.findById(event.getBankAccountId());
            if (event.getTransactionStatus().equals(TransactionStatus.PENDING)) {
                if (optionalBankAccount.isPresent()) {
                    BankAccountEntity bankAccount = optionalBankAccount.get();
                    bankAccount.setBalance(bankAccount.getBalance().add(event.getCreditAmount()));
                    this.repository.save(bankAccount);
                    bankAccountCommandService.creditMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), TransactionStatus.DONE, event.getScheduleToken());
                    bankAccountCommandService.markEventDeleted(event.getBankAccountId());
                } else {
                    bankAccountCommandService.creditMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), TransactionStatus.ERROR, event.getScheduleToken());
                    bankAccountCommandService.markEventDeleted(event.getBankAccountId());
                    throw new BankAccountNotFoundException(event.getBankAccountId());
                }
            }
        } else if (schedule.get(event) != null) {
            bankAccountCommandService.creditMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), TransactionStatus.PENDING, schedule.get(event));
        }
    }
}
