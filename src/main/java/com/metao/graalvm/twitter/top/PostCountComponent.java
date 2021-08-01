package com.metao.graalvm.twitter.top;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Component
public class PostCountComponent {

    public static final String POST_COUNT = "post-count";

    //    @Bean
//    public Function<KStream<Object, PostEvent>, KStream<?, WordCount>> test() {
//        return input -> input.flatMapValues(value -> List.of(value.getText().toLowerCase(Locale.ROOT).split("\\W+")))
//                .map(((key, value) -> new KeyValue<>(value, value)))
//                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
//                .windowedBy(TimeWindows.of(Duration.ofMillis(5000)))
//                .count(Materialized.as(PostCountComponent.POST_COUNT))
//                .toStream()
//                .map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value,
//                        new Date(key.window().start()), new Date(key.window().end()))));
//    }

    @Bean
    public Function<KStream<Object, String>, KStream<String, Long>> test() {
        return input -> input.flatMapValues(value -> List.of(value.toLowerCase(Locale.ROOT).split("\\W+")))
                .map(((key, value) -> new KeyValue<>(value, value)))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .count(Materialized.as(PostCountComponent.POST_COUNT))
                .toStream();
    }

}
