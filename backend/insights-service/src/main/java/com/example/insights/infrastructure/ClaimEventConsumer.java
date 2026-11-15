package com.example.insights.infrastructure;
import com.example.insights.service.ProjectionService;
import com.example.platform.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class ClaimEventConsumer {
 private final ObjectMapper json;private final ProjectionService projection;
 public ClaimEventConsumer(ObjectMapper json,ProjectionService projection){this.json=json;this.projection=projection;}
 @KafkaListener(topics=KafkaTopics.CLAIMS) public void receive(String value)throws Exception {projection.apply(json.readValue(value,ClaimChanged.class));}
}
