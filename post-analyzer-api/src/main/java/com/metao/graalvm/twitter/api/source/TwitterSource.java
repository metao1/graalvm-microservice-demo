package com.metao.graalvm.twitter.api.source;

import com.hazelcast.function.SupplierEx;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.logging.ILogger;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class TwitterSource implements Serializable {

    private TwitterSource() {
    }

    public static StreamSource<String> stream(
            @Nonnull Properties credential,
            @Nonnull SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
        return stream(credential, Constants.STREAM_HOST, endpointSupplier);
    }

    public static StreamSource<String> streamTimestamp(
            @Nonnull Properties credentials,
            @Nonnull SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
        return timestampedStream(credentials, Constants.STREAM_HOST, endpointSupplier);
    }

    private static StreamSource<String> timestampedStream(@Nonnull Properties credentials,
                                                          @Nonnull String streamHost,
                                                          @Nonnull SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
        return SourceBuilder.timestampedStream("twitter-timestamp-source-builder",
                        ctx -> new TwitterStreamSourceContext(credentials, streamHost, ctx.logger(), endpointSupplier))
                .fillBufferFn(TwitterStreamSourceContext::fillTimestampedBuffer)
                .destroyFn(TwitterStreamSourceContext::close)
                .build();
    }

    private static StreamSource<String> stream(@Nonnull Properties credential,
                                               @Nonnull String streamHost,
                                               @Nonnull SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
        return SourceBuilder.stream("twitter-source-builder",
                        ctx -> new TwitterStreamSourceContext(credential, streamHost, ctx.logger(), endpointSupplier))
                .fillBufferFn(TwitterStreamSourceContext::fillBuffer)
                .destroyFn(TwitterStreamSourceContext::close)
                .build();
    }

    private static final class TwitterStreamSourceContext implements Serializable {
        private static final int QUEUE_CAPACITY = 1;
        private static final int MAX_FILL_ELEMENTS = 1;
        private final ILogger log;
        private final BasicClient client;
        private final BlockingQueue<String> queue;
        private final List<String> buffer = new ArrayList<>(MAX_FILL_ELEMENTS);

        TwitterStreamSourceContext(@Nonnull Properties credential,
                                   @Nonnull String streamHost,
                                   @Nonnull ILogger logger,
                                   @Nonnull SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
            checkTwitterCreds(credential);
            log = logger;
            queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
            client = createTwitterClient(queue, streamHost, credential, endpointSupplier);
            client.connect();
            log.info("successfully connected to twitter api!");
        }

        private void fillBuffer(SourceBuilder.SourceBuffer<String> sourceBuffer) throws InterruptedException {
            queue.drainTo(buffer, MAX_FILL_ELEMENTS);
            for (String item : buffer) {
                sourceBuffer.add(item);
            }
            buffer.clear();
        }

        private void fillTimestampedBuffer(SourceBuilder.TimestampedSourceBuffer<String> sourceBuffer) throws InterruptedException {
            queue.drainTo(buffer, MAX_FILL_ELEMENTS);
            TimeUnit.SECONDS.sleep(5);
            for (String item : buffer) {
                try {
                    JsonObject jsonNode = Json.parse(item).asObject();
                    String timestampValue = jsonNode.getString("timestamp_ms", null);
                    if (timestampValue != null) {
                        long timestamp = Long.parseLong(timestampValue);
                        sourceBuffer.add(item, timestamp);
                    } else {
                        log.warning("item had no timestamp_ms");
                    }
                } catch (Exception ex) {
                    log.fine("error while parsing json object reason: {}", ex);
                }
            }
        }

        private static void checkTwitterCreds(Properties credential) {
            String consumerKey = credential.getProperty("consumerKey");
            String consumerSecret = credential.getProperty("consumerSecret");
            String token = credential.getProperty("token");
            String tokenSecret = credential.getProperty("tokenSecret");

            Objects.requireNonNull(consumerKey, "consumerKey");
            Objects.requireNonNull(consumerSecret, "consumerSecret");
            Objects.requireNonNull(token, "token");
            Objects.requireNonNull(tokenSecret, "tokenSecret");
        }

        private BasicClient createTwitterClient(BlockingQueue<String> msgQueue,
                                                String streamingHost,
                                                Properties credentials,
                                                SupplierEx<? extends StreamingEndpoint> endpointSupplier) {
            Hosts baseHost = new HttpHosts(streamingHost);
            String consumerKey = credentials.getProperty("consumerKey");
            String consumerSecret = credentials.getProperty("consumerSecret");
            String token = credentials.getProperty("token");
            String tokenSecret = credentials.getProperty("tokenSecret");

            Authentication auth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
            ClientBuilder builder = new ClientBuilder()
                    .name("client-" + Thread.currentThread().getName())// optional: mainly for the logs
                    .hosts(baseHost)
                    .authentication(auth)
                    .endpoint(endpointSupplier.get())
                    .processor(new StringDelimitedProcessor(msgQueue));

            return builder.build();
        }

        private void close() {
            if (client != null) {
                client.stop(0);
            }
        }
    }
}
