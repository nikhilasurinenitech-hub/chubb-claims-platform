package com.example.platform;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;

@Configuration
public class KafkaTopics {
  public static final String CLAIMS = "claims.changed.v1";

  @Bean
  NewTopic claimsTopic() {
    return new NewTopic(CLAIMS, 3, (short) 1)
        .configs(java.util.Map.of("cleanup.policy", "compact", "retention.ms", "-1"));
  }

  @Bean
  NewTopic deadLetterTopic() {
    return new NewTopic(CLAIMS + ".DLT", 3, (short) 1);
  }
}
