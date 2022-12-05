package com.whitebox.bankaccount.aggregate;

import com.whitebox.bankaccount.command.OpenBankAccountCommand;
import com.whitebox.bankaccount.event.OpenBankAccountEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountAggregateTest {

    private FixtureConfiguration<BankAccountAggregate> fixture;
    private UUID id;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(BankAccountAggregate.class);
        id = UUID.randomUUID();
    }

    @Test
    public void should_dispatch_accountcreated_event_when_createaccount_command() {
        fixture.givenNoPriorActivity()
                .when(new OpenBankAccountCommand(
                        id,
                        BigDecimal.valueOf(1000),
                        "Marko",
                        BigDecimal.valueOf(100))
                )
                .expectEvents(new OpenBankAccountEvent(
                        id,
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(100),
                        "Marko")
                );
    }
}
