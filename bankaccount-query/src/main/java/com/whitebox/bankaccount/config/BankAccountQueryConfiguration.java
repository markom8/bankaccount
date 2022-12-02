package com.whitebox.bankaccount.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankAccountQueryConfiguration {

    /************************************************/
    /* Register interceptors on the bus             */

    /************************************************/

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerDefaultHandlerInterceptor((t, u) -> new LoggingInterceptor<>(u));
    }
}
