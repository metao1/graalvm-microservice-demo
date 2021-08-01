package com.metao.graalvm.twitter.api;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JetConfiguration {

    @Bean
    public JetInstance jet() {
        return Jet.bootstrappedInstance();
    }
}
