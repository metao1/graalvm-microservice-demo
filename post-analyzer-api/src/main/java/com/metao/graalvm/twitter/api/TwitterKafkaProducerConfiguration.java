package com.metao.graalvm.twitter.api;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter")
public class TwitterKafkaProducerConfiguration {

    String consumerKey;

    String consumerSecret;

    String token;

    String secret;

    List<String> topics;

}
