package com.whitebox.bankaccount.aggregate;

import com.whitebox.bankaccount.command.CreditBankAccountCommand;
import com.whitebox.bankaccount.command.DebitBankAccountCommand;
import com.whitebox.bankaccount.command.DeleteBankAccountCommand;
import com.whitebox.bankaccount.command.OpenBankAccountCommand;
import com.whitebox.bankaccount.event.CreditBankAccountEvent;
import com.whitebox.bankaccount.event.DebitBankAccountEvent;
import com.whitebox.bankaccount.event.OpenBankAccountEvent;
import com.whitebox.bankaccount.event.TransactionStatus;
import com.whitebox.bankaccount.event.closed.BankAccountClosedEvent;
import com.whitebox.bankaccount.event.scheduled.CreditBankAccountScheduledEvent;
import com.whitebox.bankaccount.event.scheduled.DebitBankAccountScheduledEvent;
import com.whitebox.bankaccount.exception.InsufficientBankAccountBalanceException;
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
    private TransactionStatus transactionStatus;

    @CommandHandler
    public BankAccountAggregate(OpenBankAccountCommand command) {

        AggregateLifecycle.apply(
                new OpenBankAccountEvent(
                        command.getBankAccountId(),
                        command.getInitialDeposite(),
                        command.getUser()
                )
        );
    }

    @EventSourcingHandler
    public void on(OpenBankAccountEvent event) {
        this.bankAccountId = event.getBankAccountId();
        this.user = event.getUser();
        this.balance = event.getInitialBalance();
        this.transactionStatus = event.getTransactionStatus();
    }

    @CommandHandler
    public void handle(CreditBankAccountCommand command) {
        if (command.getTransactionStatus() == null && command.getScheduleToken() == null) {
            AggregateLifecycle.apply(
                    new CreditBankAccountScheduledEvent(
                            command.getBankAccountId(),
                            command.getCreditAmount(),
                            command.getExecutionDateTime(),
                            TransactionStatus.PENDING,
                            null
                    )
            );
        } else if (command.getTransactionStatus() != null && command.getScheduleToken() != null) {
            AggregateLifecycle.apply(
                    new CreditBankAccountEvent(
                            command.getBankAccountId(),
                            command.getCreditAmount(),
                            command.getExecutionDateTime(),
                            command.getTransactionStatus(),
                            command.getScheduleToken()
                    )
            );
        }
    }

    @EventSourcingHandler
    public void on(CreditBankAccountEvent event) {
        this.balance = this.balance.add(event.getCreditAmount());
        this.transactionStatus = event.getTransactionStatus();
    }

    @CommandHandler
    public void handle(DebitBankAccountCommand command) {
        if (command.getTransactionStatus() == null && command.getScheduleToken() == null) {
            AggregateLifecycle.apply(
                    new DebitBankAccountScheduledEvent(
                            command.getBankAccountId(),
                            command.getDebitAmount(),
                            command.getExecutionDateTime(),
                            TransactionStatus.PENDING,
                            null
                    )
            );
        } else if (command.getTransactionStatus() != null && command.getScheduleToken() != null) {
            AggregateLifecycle.apply(
                    new DebitBankAccountEvent(
                            command.getBankAccountId(),
                            command.getDebitAmount(),
                            command.getExecutionDateTime(),
                            command.getTransactionStatus(),
                            command.getScheduleToken()
                    )
            );
        }
//        AggregateLifecycle.apply(
//                new DebitBankAccountScheduledEvent(
//                        command.getBankAccountId(),
//                        command.getDebitAmount(),
//                        command.getExecutionDateTime()
//                )
//        );
    }

    @EventSourcingHandler
    public void on(DebitBankAccountEvent event) throws InsufficientBankAccountBalanceException {
        if (this.balance.compareTo(event.getDebitAmount()) < 0) {
            throw new InsufficientBankAccountBalanceException(event.getBankAccountId(), event.getDebitAmount());
        }
        this.balance = this.balance.subtract(event.getDebitAmount());
        this.transactionStatus = event.getTransactionStatus();
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
