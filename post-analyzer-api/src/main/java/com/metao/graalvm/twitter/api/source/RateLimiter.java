package com.metao.graalvm.twitter.api.source;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.StreamStage;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeMap;

@Component
public class RateLimiter {

    public <T, S extends StreamStage<T>> FunctionEx<S, S> throttle(int itemsPerSecond) {
        // context for the mapUsingService stage
        class Service implements Serializable {
            final int ratePerSecond;
            final TreeMap<Long, Long> counts = new TreeMap<>(
                    (Comparator<Long> & Serializable) Long::compareTo
            );

            public Service(int ratePerSecond) {
                this.ratePerSecond = ratePerSecond;
            }
        }
        FunctionEx<Processor.Context, Service> func = procCtx ->
                // divide the count for the actual number of processors we have
                new Service(Math.max(1, itemsPerSecond / procCtx.totalParallelism()));
        // factory for the service
        ServiceFactory<?, Service> serviceFactory = ServiceFactories
                .nonSharedService(func)
                // non-cooperative is needed because we sleep in the mapping function
                .toNonCooperative();

        return stage -> (S) stage
                .mapUsingService(serviceFactory,
                        (ctx, item) -> {
                            // current time in 10ths of a second
                            long now = System.nanoTime() / 100_000_000;
                            // include this item in the counts
                            //ctx.counts.merge(now, 1L, Long::sum);
                            // clear items emitted more than second ago
                            //ctx.counts.headMap(now - 10, true).clear();
//                            long countInLastSecond =
//                                    ctx.counts.values().stream().mapToLong(Long::longValue).sum();
                            // if we emitted too many items, sleep a while
//                            if (countInLastSecond > ctx.ratePerSecond) {
//                                Thread.sleep(
//                                        (countInLastSecond - ctx.ratePerSecond) * 1000 / ctx.ratePerSecond);
//                            }
                            // now we can pass the item on
                            return item;
                        }
                );
    }

}
