package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.ServiceUtils;
import com.whitebox.bankaccount.event.debit.DebitBankAccountConstructEvent;
import com.whitebox.bankaccount.event.scheduled.DebitBankAccountScheduledEvent;
import com.whitebox.bankaccount.exception.BankAccountScheduleException;
import com.whitebox.bankaccount.service.BankAccountCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankAccountScheduleDebitSaga {
    private final EventScheduler eventScheduler;
    private final BankAccountCommandService bankAccountCommandService;

    private Map<ScheduleToken, DebitBankAccountConstructEvent> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "bankAccountId")
    public void on(DebitBankAccountScheduledEvent event) throws BankAccountScheduleException {
        log.info("Handling a Scheduled Bank Account Debit command {}", event.getBankAccountId().toString());
        if (!event.getExecutionDateTime().isBefore(ZonedDateTime.now())) {
            log.info("Schedule a Bank Account Debit command {}", event.getBankAccountId().toString());
            DebitBankAccountConstructEvent DebitBankAccountEvent = new DebitBankAccountConstructEvent(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), null);
            ScheduleToken scheduleToken = eventScheduler.schedule(event.getExecutionDateTime().toInstant(), DebitBankAccountEvent);
            schedule.put(scheduleToken, DebitBankAccountEvent);
            log.info("Bank Account Debit command scheduled {}", event.getBankAccountId().toString());
        } else {
            bankAccountCommandService.debitMoneyToAccountCompleted(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "Execution time exception!");
            throw new BankAccountScheduleException(event.getBankAccountId(), event.getExecutionDateTime());
        }
    }

    @SagaEventHandler(associationProperty = "bankAccountId")
    public void handle(final DebitBankAccountConstructEvent event) {
        if (schedule.containsValue(event)) {
            log.info("Construct a Scheduled Bank Account Debit command {}", event.getBankAccountId().toString());
            bankAccountCommandService.debitMoneyToAccountEvent(event.getBankAccountId(), event.getDebitAmount(), event.getExecutionDateTime(), ServiceUtils.keys(schedule, event).findFirst().get());
            endSaga();
        }
    }

    @EndSaga
    private void endSaga() {
        log.info("Schedule Debit process completed.");
    }
}
