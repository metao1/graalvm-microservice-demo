package com.metao.graalvm.twitter.top.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.kafka.utils.DlqDestinationResolver;
import org.springframework.context.annotation.Bean;

@Slf4j
public class DeserializationExceptionHandler {

    @Bean
    public DlqDestinationResolver dlqDestinationResolver() {
        return (rec, ex) -> {
            log.error(ex.getMessage());
            return ex.getMessage();
        };
    }
}
