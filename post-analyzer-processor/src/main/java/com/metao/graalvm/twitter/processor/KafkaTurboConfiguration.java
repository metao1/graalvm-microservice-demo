package com.metao.graalvm.twitter.processor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaTurboConfiguration {

    private List<String> bootstrapServers;
    private String schemaRegistryUrl;
    private TurboKafkaConsumer consumer = new TurboKafkaConsumer();
    private TurboKafkaProducer producer = new TurboKafkaProducer();

    @Data
    public static class TurboKafkaConsumer {
        private String groupId;
        private String autoOffsetReset;
        private String enableAutoCommit;
        private String keyDeserializer;
        private String valueDeserializer;
        private String interceptorClass;
        private TurboKafkaTopic topic;
    }

    @Data
    public static class TurboKafkaProducer {
        private String keySerializer;
        private String valueSerializer;
        private String interceptorClass;
        private TurboKafkaTopic topic;
    }

    @Data
    public static class TurboKafkaTopic {
        private String name;
        private int partitionNum;
        private int replicationFactor;
    }
}