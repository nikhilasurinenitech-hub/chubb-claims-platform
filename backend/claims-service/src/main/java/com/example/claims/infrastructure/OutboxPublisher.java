package com.example.claims.infrastructure;
import com.example.platform.KafkaTopics;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.TimeUnit;
@Component @ConditionalOnProperty(name="outbox.enabled",havingValue="true",matchIfMissing=true)
public class OutboxPublisher {
 private final JdbcTemplate jdbc;private final KafkaTemplate<String,String> kafka;private final TransactionTemplate tx;
 public OutboxPublisher(JdbcTemplate jdbc,KafkaTemplate<String,String> kafka,TransactionTemplate tx){this.jdbc=jdbc;this.kafka=kafka;this.tx=tx;}
 @Scheduled(fixedDelayString="${outbox.delay-ms:1000}") public void publish() {
  // Acknowledgement precedes published_at. A crash may repeat delivery; consumers must deduplicate.
  try {tx.executeWithoutResult(status->{
   var rows=jdbc.queryForList("select id,aggregate_id,payload from outbox_event where published_at is null order by created_at,aggregate_version limit 25 for update skip locked");
   for(var row:rows) {try {kafka.send(KafkaTopics.CLAIMS,row.get("aggregate_id").toString(),row.get("payload").toString()).get(12,TimeUnit.SECONDS);jdbc.update("update outbox_event set published_at=CURRENT_TIMESTAMP where id=?",row.get("id"));}catch(Exception e){if(e instanceof InterruptedException)Thread.currentThread().interrupt();throw new IllegalStateException("Event publication deferred",e);}}
  });}catch(Exception e){LoggerFactory.getLogger(getClass()).warn("Outbox retains pending events for retry: {}",e.getMessage());}
 }
}
