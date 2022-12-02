package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.BankAccountRepository;
import com.whitebox.bankaccount.event.DebitBankAccountEvent;
import com.whitebox.bankaccount.event.TransactionStatus;
import com.whitebox.bankaccount.event.scheduled.DebitBankAccountScheduledEvent;
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
public class BankAccountDebitSaga {
    private final EventScheduler eventScheduler;
    private final BankAccountCommandService bankAccountCommandService;
    private final BankAccountRepository repository;
    private final EventStore eventStore;

    private Map<DebitBankAccountEvent, String> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "bankAccountId")
    public void on(DebitBankAccountScheduledEvent event) {
        log.debug("Handling a Scheduled Bank Account Debit command {}", event.getBankAccountId().toString());
        if (event.getExecutionDateTime().isAfter(LocalDateTime.now())) {
            DebitBankAccountEvent debitBankAccountEvent = new DebitBankAccountEvent(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), TransactionStatus.PENDING, null);
            ScheduleToken scheduleToken = eventScheduler.schedule(event.getExecutionDateTime().atZone(ZoneId.systemDefault()).toInstant(), debitBankAccountEvent);
            schedule.put(debitBankAccountEvent, scheduleToken.toString());
        }
    }

    @SagaEventHandler(associationProperty = "bankAccountId")
    public void handle(final DebitBankAccountEvent event) throws BankAccountNotFoundException {
        if (event.getScheduleToken() != null) {
            log.debug("Handling a Bank Account Debit command {}", event.getBankAccountId());
            Optional<BankAccountEntity> optionalBankAccount = this.repository.findById(event.getBankAccountId());
            if (event.getTransactionStatus().equals(TransactionStatus.PENDING)) {
                if (optionalBankAccount.isPresent()) {
                    BankAccountEntity bankAccount = optionalBankAccount.get();
                    bankAccount.setBalance(bankAccount.getBalance().subtract(event.getDebitAmount()));
                    this.repository.save(bankAccount);
                    bankAccountCommandService.debitMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), TransactionStatus.DONE, event.getScheduleToken());
                    bankAccountCommandService.markEventDeleted(event.getBankAccountId());
                } else {
                    bankAccountCommandService.debitMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), TransactionStatus.ERROR, event.getScheduleToken());
                    bankAccountCommandService.markEventDeleted(event.getBankAccountId());
                    throw new BankAccountNotFoundException(event.getBankAccountId());
                }
            }
        } else if (schedule.get(event) != null) {
            bankAccountCommandService.debitMoneyToAccountTransactionStatus(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), TransactionStatus.PENDING, schedule.get(event));
        }
    }
}
