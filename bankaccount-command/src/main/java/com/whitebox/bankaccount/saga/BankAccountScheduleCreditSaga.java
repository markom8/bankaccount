package com.whitebox.bankaccount.saga;

import com.whitebox.bankaccount.ServiceUtils;
import com.whitebox.bankaccount.event.credit.CreditBankAccountConstructEvent;
import com.whitebox.bankaccount.event.scheduled.CreditBankAccountScheduledEvent;
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
public class BankAccountScheduleCreditSaga {
    private final EventScheduler eventScheduler;
    private final BankAccountCommandService bankAccountCommandService;

    private Map<ScheduleToken, CreditBankAccountConstructEvent> schedule = new HashMap<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "bankAccountId")
    public void on(CreditBankAccountScheduledEvent event) throws BankAccountScheduleException {
        log.info("Handling a Scheduled Bank Account Credit command {}", event.getBankAccountId().toString());
        if (!event.getExecutionDateTime().isBefore(ZonedDateTime.now())) {
            log.info("Schedule a Bank Account Credit command {}", event.getBankAccountId().toString());
            CreditBankAccountConstructEvent creditBankAccountEvent = new CreditBankAccountConstructEvent(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), null);
            ScheduleToken scheduleToken = eventScheduler.schedule(event.getExecutionDateTime().toInstant(), creditBankAccountEvent);
            schedule.put(scheduleToken, creditBankAccountEvent);
            log.info("Bank Account Credit command scheduled {}", event.getBankAccountId().toString());
        } else {
            bankAccountCommandService.creditMoneyToAccountCompleted(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), event.getScheduleToken(), "Execution time exception!");
            throw new BankAccountScheduleException(event.getBankAccountId(), event.getExecutionDateTime());
        }
    }

    @SagaEventHandler(associationProperty = "bankAccountId")
    public void handle(final CreditBankAccountConstructEvent event) {
        if (schedule.containsValue(event)) {
            log.info("Construct a Scheduled Bank Account Credit command {}", event.getBankAccountId().toString());
            bankAccountCommandService.creditMoneyToAccountEvent(event.getBankAccountId(), event.getCreditAmount(), event.getExecutionDateTime(), ServiceUtils.keys(schedule, event).findFirst().get());
            endSaga();
        }
    }

    @EndSaga
    private void endSaga() {
        log.info("Schedule credit process completed.");
    }
}
