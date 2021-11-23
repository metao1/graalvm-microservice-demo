package com.metao.graalvm.twitter.processor.connectors;


import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Observable;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.WindowDefinition;
import com.metao.graalvm.twitter.processor.utils.KafkaUtils;
import com.metao.graalvm.twitter.processor.config.KafkaTurboConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Slf4j
@Service
public class TwitterKafkaConnector {

    private final Observable<Map.Entry<HazelcastJsonValue, HazelcastJsonValue>> observable;
    private final Observable<HazelcastJsonValue> stringObservable;
    private final KafkaTurboConfiguration kafkaTurboConfiguration;
    private final JetInstance jet;

    public TwitterKafkaConnector(JetInstance jet, KafkaTurboConfiguration kafkaTurboConfiguration) {
        this.jet = jet;
        this.kafkaTurboConfiguration = kafkaTurboConfiguration;
        this.observable = jet.newObservable();
        this.stringObservable = jet.newObservable();
    }

    @PostConstruct
    public void setup() {
        observable.addObserver(this::dataSinkObserver);
        Executors.defaultThreadFactory().newThread(this::initJet).start();
    }

    private void initJet() {
        final Pipeline pipeline = Pipeline.create();
        final String consumerTopic = kafkaTurboConfiguration.getConsumer().getTopic().getName();
        final String producerTopic = kafkaTurboConfiguration.getProducer().getTopic().getName();
        extracted(pipeline, consumerTopic, producerTopic);
        try {
            jet.newJob(pipeline).join();
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        }
    }

    private void extracted(Pipeline pipeline, String consumerTopic, String producerTopic) {
        pipeline.readFrom(KafkaSources.kafka(jet.getConfig().getProperties(),
                        KafkaUtils::mapToHazelcastJsonValue, consumerTopic))
                .withNativeTimestamps(0)
                .window(WindowDefinition.session(10000))
                .streamStage()
                .peek()
                .filter(Objects::nonNull)
                .map(Map.Entry::getValue)
                .writeTo(KafkaSinks.kafka(loadKafkaProps(), producerTopic, HazelcastJsonValue::toString, HazelcastJsonValue::toString));
    }

    private Properties loadKafkaProps() {
        Properties properties = new Properties();
        String bootstrapServers = String.join(", ", kafkaTurboConfiguration.getBootstrapServers());
        String keySerializerClass = kafkaTurboConfiguration.getProducer().getKeySerializer();
        String valueSerializerClass = kafkaTurboConfiguration.getProducer().getValueSerializer();
        String schemaRegistryServer = kafkaTurboConfiguration.getSchemaRegistryUrl();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("schema.registry.url", schemaRegistryServer);
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        return properties;
    }

    private void dataSinkObserver(Map.Entry<HazelcastJsonValue, HazelcastJsonValue> record) {
        String recordKey = record.getKey().toString();
        log.info("record key :{}, record value: {}", record.getKey(), record.getValue());
    }

}
