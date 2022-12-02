package com.whitebox.bankaccount.controller;


import com.whitebox.bankaccount.BankAccountEntity;
import com.whitebox.bankaccount.event.BankAccountEvent;
import com.whitebox.bankaccount.service.BankAccountQueryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/accounts")
@AllArgsConstructor
public class BankAccountQueryController {

    private final BankAccountQueryService bankAccountQueryService;

    @GetMapping("/{accountId}")
    public CompletableFuture<BankAccountEntity> findById(@PathVariable("accountId") String accountId) {
        return this.bankAccountQueryService.findById(accountId);
    }

    @GetMapping("/{accountId}/events")
    public List<BankAccountEvent> listEventsForAccount(@PathVariable(value = "accountId") String accountId) {
        return (List<BankAccountEvent>) this.bankAccountQueryService.listEventsForAccount(accountId);
    }

//    @GetMapping("/{accountId}/events/scheduled_from/{executionDateTime}")
//    public List<BankAccountEvent> listEventsForAccount(@PathVariable(value = "accountId") String accountId,
//                                                       @PathVariable(value = "executionDateTime") String executionDateTimeString) {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime executionDateTime = LocalDateTime.parse(executionDateTimeString, formatter);
//
//        List<BankAccountEvent> bankAccountEvents = (List<BankAccountEvent>) this.bankAccountQueryService.listEventsForAccount(accountId);
//        return bankAccountEvents
//                .stream()
//                .filter(bankAccountEvent -> bankAccountEvent.getExecutionDateTime().isAfter(executionDateTime))
//                .collect(Collectors.toList());
//    }
}
