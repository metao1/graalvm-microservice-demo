package com.metao.graalvm.twitter.top;

import lombok.AllArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PageCountController {

    private final InteractiveQueryService interactiveQueryService;

    @GetMapping("/counts")
    public Map<String, Long> count() {
        ReadOnlyKeyValueStore<String, Long> queryableStore = interactiveQueryService
                .getQueryableStore(PostCountComponent.POST_COUNT, QueryableStoreTypes.keyValueStore());
        KeyValueIterator<String, Long> allKeyValues = queryableStore.all();
        Map<String, Long> allKeyValueMaps = new LinkedHashMap<>();
        while (allKeyValues != null && allKeyValues.hasNext()) {
            KeyValue<String, Long> next = allKeyValues.next();
            allKeyValueMaps.put(next.key, next.value);
        }
        return allKeyValueMaps;
    }

}
