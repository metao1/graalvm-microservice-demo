package com.metao.graalvm.twitter.processor.config;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Configuration
public class JetInstanceConfiguration {

    private final KafkaTurboConfiguration configuration;

    JetInstanceConfiguration(KafkaTurboConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public JetInstance jet() {
        JetConfig jetConfig = new JetConfig();
        jetConfig.setProperties(consumerPipelineProperties());
        return Jet.newJetInstance(jetConfig);
    }

    private Properties consumerPipelineProperties() {
        Properties properties = new Properties();

        KafkaTurboConfiguration.TurboKafkaConsumer consumer = configuration.getConsumer();
        String bootstrapServers = String.join(", ", configuration.getBootstrapServers());
        String keyDeserializerClass = configuration.getConsumer().getKeyDeserializer();
        String valueDeserializerClass = configuration.getConsumer().getValueDeserializer();
        String autoOffsetReset = configuration.getConsumer().getAutoOffsetReset();
        String enableAutoCommit = configuration.getConsumer().getEnableAutoCommit();
        String schemaRegistryServer = configuration.getSchemaRegistryUrl();
        properties.setProperty("group.id", consumer.getGroupId());
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("schema.registry.url", schemaRegistryServer);
        properties.setProperty("enable.auto.commit", enableAutoCommit);
        properties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        properties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        properties.setProperty("auto.offset.reset", autoOffsetReset);
        return properties;
    }
}
