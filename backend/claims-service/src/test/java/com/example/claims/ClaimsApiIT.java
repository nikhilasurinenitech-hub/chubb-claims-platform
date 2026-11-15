package com.example.claims;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"outbox.enabled=false","spring.kafka.admin.auto-create=false"})
class ClaimsApiIT {
 @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:17-alpine");
 @DynamicPropertySource static void db(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->postgres.getJdbcUrl()+"&currentSchema=claims");r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);}
 @Autowired TestRestTemplate http;@Autowired JdbcTemplate jdbc;
 final String password="ClaimsDemo!2026";
 ResponseEntity<JsonNode> create(String user,String key,String description){var h=new HttpHeaders();h.setContentType(MediaType.APPLICATION_JSON);h.set("Idempotency-Key",key);return http.withBasicAuth(user,password).exchange("/api/claims",HttpMethod.POST,new HttpEntity<>(Map.of("type","MOTOR","market","SG","incidentDate","2026-01-01","location","Singapore","description",description,"requestedAmount",5000),h),JsonNode.class);}
 JsonNode newClaim(){var r=create("maya",UUID.randomUUID().toString(),"Vehicle damaged while parked");assertEquals(201,r.getStatusCode().value());return r.getBody();}
 ResponseEntity<JsonNode> act(String id,String action,long version,String note,Integer amount,String user){var body=new HashMap<String,Object>();body.put("version",version);body.put("note",note);body.put("amount",amount);return http.withBasicAuth(user,password).postForEntity("/api/claims/"+id+"/"+action,body,JsonNode.class);}
 @Test void idempotencyAndOwnershipAreEnforced(){String key=UUID.randomUUID().toString();var first=create("maya",key,"Vehicle damaged while parked");String id=first.getBody().path("claim").path("id").asText();assertEquals(200,create("maya",key,"Vehicle damaged while parked").getStatusCode().value());assertEquals(409,create("maya",key,"Different incident details").getStatusCode().value());assertEquals(404,http.withBasicAuth("leo",password).getForEntity("/api/claims/"+id,JsonNode.class).getStatusCode().value());assertEquals(403,create("alex",UUID.randomUUID().toString(),"Staff cannot submit claims").getStatusCode().value());assertEquals(401,http.getForEntity("/api/claims",JsonNode.class).getStatusCode().value());assertEquals(1,jdbc.queryForObject("select count(*) from outbox_event where aggregate_id=?",Integer.class,UUID.fromString(id)));}
 @Test void completesInformationAssessmentAndSettlementAtomically(){var d=newClaim();String id=d.path("claim").path("id").asText();assertEquals(200,act(id,"assign",0,"",null,"alex").getStatusCode().value());assertEquals(409,act(id,"assign",0,"",null,"priya").getStatusCode().value());assertEquals(403,act(id,"assessment",1,"Reviewed estimate",4000,"priya").getStatusCode().value());var req=act(id,"information-requests",1,"Please provide repair details",null,"alex");assertEquals(200,req.getStatusCode().value());String request=req.getBody().path("informationRequests").get(0).path("id").asText();var response=http.withBasicAuth("maya",password).postForEntity("/api/claims/"+id+"/information-requests/"+request+"/response",Map.of("version",2,"answer","Repair estimate totals 4000 SGD"),JsonNode.class);assertEquals(200,response.getStatusCode().value());assertEquals(200,act(id,"assessment",3,"Estimate reviewed and accepted",4000,"alex").getStatusCode().value());assertEquals(200,act(id,"approve",4,"Approved",null,"alex").getStatusCode().value());assertEquals(400,act(id,"settle",5,"Invalid settlement",5000,"alex").getStatusCode().value());var settled=act(id,"settle",5,"Settlement decision recorded",3800,"alex");assertEquals(200,settled.getStatusCode().value());assertEquals("SETTLED",settled.getBody().path("claim").path("status").asText());assertEquals(7,settled.getBody().path("history").size());assertEquals(7,jdbc.queryForObject("select count(*) from outbox_event where aggregate_id=?",Integer.class,UUID.fromString(id)));}
 @Test void rejectionNeedsReasonAndCannotBeReopened(){String id=newClaim().path("claim").path("id").asText();act(id,"assign",0,"",null,"alex");assertEquals(400,act(id,"reject",1,"",null,"alex").getStatusCode().value());var rejected=act(id,"reject",1,"Incident outside the demo coverage period",null,"alex");assertEquals(200,rejected.getStatusCode().value());assertEquals("REJECTED",rejected.getBody().path("claim").path("status").asText());assertEquals(409,act(id,"information-requests",2,"More details requested",null,"alex").getStatusCode().value());}
}
