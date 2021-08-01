package com.metao.graalvm.twitter.processor;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

@Slf4j
public class KafkaUtils {

    public static <K, V> Map.Entry<HazelcastJsonValue, HazelcastJsonValue> mapToHazelcastJsonValue(ConsumerRecord<K, V> record) {
        try {
            if (record.key() == null) {
                log.warn("key was null");
            }
            if (record.value() == null) {
                log.warn("value was null");
            }
            HazelcastJsonValue key = new HazelcastJsonValue(record.key().toString());
            HazelcastJsonValue value = new HazelcastJsonValue(record.value().toString());
            return Util.entry(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
