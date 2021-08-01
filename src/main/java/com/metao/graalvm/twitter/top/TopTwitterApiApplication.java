package com.metao.graalvm.twitter.top;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TopTwitterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TopTwitterApiApplication.class, args);
    }
}
