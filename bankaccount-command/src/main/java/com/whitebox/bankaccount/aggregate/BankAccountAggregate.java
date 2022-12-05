package com.whitebox.bankaccount.aggregate;

import com.whitebox.bankaccount.command.*;
import com.whitebox.bankaccount.event.OpenBankAccountEvent;
import com.whitebox.bankaccount.event.closed.BankAccountClosedEvent;
import com.whitebox.bankaccount.event.credit.CreditBankAccountCompletedEvent;
import com.whitebox.bankaccount.event.credit.CreditBankAccountEvent;
import com.whitebox.bankaccount.event.debit.DebitBankAccountCompletedEvent;
import com.whitebox.bankaccount.event.debit.DebitBankAccountEvent;
import com.whitebox.bankaccount.event.scheduled.CreditBankAccountScheduledEvent;
import com.whitebox.bankaccount.event.scheduled.DebitBankAccountScheduledEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private UUID bankAccountId;
    private BigDecimal balance;
    private String user;
    private BigDecimal overdraftLimit;

    @CommandHandler
    public BankAccountAggregate(OpenBankAccountCommand command) {

        AggregateLifecycle.apply(
                new OpenBankAccountEvent(
                        command.getBankAccountId(),
                        command.getInitialDeposite(),
                        command.getOverdraftLimit(),
                        command.getUser()
                )
        );
    }

    @EventSourcingHandler
    public void on(OpenBankAccountEvent event) {
        this.bankAccountId = event.getBankAccountId();
        this.user = event.getUser();
        this.balance = event.getInitialBalance();
        this.overdraftLimit = event.getOverdraftLimit();
    }

    @CommandHandler
    public void handle(CreditBankAccountCommand command) {
        if (command.getScheduleToken() == null) {
            AggregateLifecycle.apply(
                    new CreditBankAccountScheduledEvent(
                            command.getBankAccountId(),
                            command.getCreditAmount(),
                            command.getExecutionDateTime(),
                            null
                    )
            );
        } else {
            AggregateLifecycle.apply(
                    new CreditBankAccountEvent(
                            command.getBankAccountId(),
                            command.getCreditAmount(),
                            command.getExecutionDateTime(),
                            command.getScheduleToken()
                    )
            );
        }
    }

    @CommandHandler
    public void handle(CreditBankAccountCompletedCommand command) {
        AggregateLifecycle.apply(
                new CreditBankAccountCompletedEvent(
                        command.getBankAccountId(),
                        command.getCreditAmount(),
                        command.getStatus(),
                        command.getExecutionDateTime(),
                        command.getScheduleToken()
                )
        );
    }

    @CommandHandler
    public void handle(DebitBankAccountCommand command) {
        if (command.getScheduleToken() == null) {
            AggregateLifecycle.apply(
                    new DebitBankAccountScheduledEvent(
                            command.getBankAccountId(),
                            command.getDebitAmount(),
                            command.getExecutionDateTime(),
                            null
                    )
            );
        } else {
            AggregateLifecycle.apply(
                    new DebitBankAccountEvent(
                            command.getBankAccountId(),
                            command.getDebitAmount(),
                            command.getExecutionDateTime(),
                            command.getScheduleToken()
                    )
            );
        }
    }

    @CommandHandler
    public void handle(DebitBankAccountCompletedCommand command) {
        AggregateLifecycle.apply(
                new DebitBankAccountCompletedEvent(
                        command.getBankAccountId(),
                        command.getDebitAmount(),
                        command.getStatus(),
                        command.getExecutionDateTime(),
                        command.getScheduleToken()
                )
        );
    }

    @CommandHandler
    public void handle(DeleteBankAccountCommand command) {
        AggregateLifecycle.apply(
                new BankAccountClosedEvent(command.getBankAccountId()));
    }

    @EventSourcingHandler
    protected void on(BankAccountClosedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
