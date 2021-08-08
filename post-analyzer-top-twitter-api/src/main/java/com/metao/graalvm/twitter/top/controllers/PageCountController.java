package com.metao.graalvm.twitter.top.controllers;

import com.metao.graalvm.twitter.top.components.PostCountComponent;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class PageCountController {

    private final InteractiveQueryService interactiveQueryService;
    private final MeterRegistry meterRegistry;
    private final Counter visitCounter;

    PageCountController(MeterRegistry meterRegistry, InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
        this.meterRegistry = meterRegistry;
        visitCounter = Counter.builder("visit_counter")
                .description("Number of visits to the site")
                .register(meterRegistry);
    }

    @Timed
    @GetMapping("/counts")
    public Map<Long, Set<String>> count() {
        visitCounter.increment();
        meterRegistry.counter("visit_counter", String.format(visitCounter.count() + "", ""));
        ReadOnlyKeyValueStore<String, Long> queryableStore = interactiveQueryService
                .getQueryableStore(PostCountComponent.POST_COUNT, QueryableStoreTypes.keyValueStore());
        KeyValueIterator<String, Long> allKeyValues = queryableStore.all();
        Map<String, Long> allKeyValueMaps = new ConcurrentHashMap<>();
        while (allKeyValues != null && allKeyValues.hasNext()) {
            KeyValue<String, Long> next = allKeyValues.next();
            allKeyValueMaps.put(next.key, next.value);
        }

        return allKeyValueMaps.entrySet()
                .parallelStream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));
    }

}
