package com.metao.graalvm.twitter.api;

import com.hazelcast.internal.json.Json;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Observable;
import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.StreamSource;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Slf4j
@Service
public class TwitterJetSource {

    private final JetInstance jet;
    private final Environment environment;
    private final TwitterKafkaProducerConfiguration twitterProps;
    private final KafkaTurboConfiguration kafkaTurboConfiguration;

    private final Observable<String> observer;

    TwitterJetSource(Environment environment, JetInstance jet, KafkaTurboConfiguration kafkaTurboConfiguration, TwitterKafkaProducerConfiguration twitterKafkaProducerConfiguration) {
        this.jet = jet;
        this.environment = environment;
        this.kafkaTurboConfiguration = kafkaTurboConfiguration;
        this.observer = jet.newObservable();
        this.twitterProps = twitterKafkaProducerConfiguration;
    }

    @PostConstruct
    public void init() {
        observer.addObserver(this::dataSinkObserver);
        observer.addObserver(this::forwardToKafka);
        Executors.defaultThreadFactory().newThread(this::handleTwitterBuffer).start();
    }

    private void forwardToKafka(String value) {

    }

    private void dataSinkObserver(String value) {
        log.info(value);
    }

    private void handleTwitterBuffer() {
        Pipeline pipeline = Pipeline.create();
        Properties twitterProps = loadTwitterProps();
        List<String> terms = this.twitterProps.getTopics();
        String sinkTopic = kafkaTurboConfiguration.getProducer().getTopic().getName();

        final StreamSource<String> streamingEndpoint = TwitterSource.streamTimestamp(
                twitterProps, () -> new StatusesFilterEndpoint().trackTerms(terms));

        pipeline.readFrom(streamingEndpoint)
                .withNativeTimestamps(0)
                .peek()
                .filter(Objects::nonNull)
                .map(rawJson -> Json.parse(rawJson)
                        .asObject()
                        .getString("text", null))
                .writeTo(KafkaSinks.kafka(loadKafkaProps(), sinkTopic, (key) -> key, (value) -> value));

        try {
            jet.newJob(pipeline).join();
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        }
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

    private Properties loadTwitterProps() {
        Properties twitterProperties = new Properties();
        String consumerKey = environment.getProperty("TWITTER_CONSUMER_KEY");
        String consumerSecret = twitterProps.getConsumerSecret();
        twitterProperties.setProperty("consumerKey", consumerKey);
        twitterProperties.setProperty("consumerSecret", consumerSecret);
        twitterProperties.setProperty("token", twitterProps.getToken());
        twitterProperties.setProperty("tokenSecret", twitterProps.getSecret());
        return twitterProperties;
    }
}
