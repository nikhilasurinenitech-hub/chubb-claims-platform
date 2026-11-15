package com.example.insights;
import com.example.insights.service.*;
import com.example.platform.ClaimChanged;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers @SpringBootTest(properties={"spring.kafka.listener.auto-startup=false","spring.kafka.admin.auto-create=false"})
class ProjectionIT {
 @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:17-alpine");
 @DynamicPropertySource static void db(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->postgres.getJdbcUrl()+"&currentSchema=insights");r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);}
 @Autowired ProjectionService projection;@Autowired InsightsService insights;@Autowired JdbcTemplate jdbc;
 @BeforeEach void clean(){jdbc.update("delete from claim_projection");}
 ClaimChanged event(UUID id,long version,String currency,String status,int reserve,int paid){Instant start=Instant.parse("2026-01-01T00:00:00Z");return new ClaimChanged(UUID.randomUUID().toString(),1,id,version,"TEST",status,currency.equals("SGD")?"SG":"AU",currency,BigDecimal.valueOf(reserve),BigDecimal.valueOf(paid),"alex",start,start.plusSeconds(7200),status.equals("SETTLED")?start.plusSeconds(7200):null);}
 @Test void deduplicatesAndIgnoresOlderSnapshots(){UUID id=UUID.randomUUID();var current=event(id,3,"SGD","UNDER_REVIEW",1000,0);assertEquals(1,projection.apply(current));assertEquals(0,projection.apply(current));assertEquals(0,projection.apply(event(id,1,"SGD","SUBMITTED",0,0)));var d=insights.dashboard(null);assertEquals(1,d.totalClaims());assertEquals(0,new BigDecimal("1000").compareTo(d.exposure().getFirst().outstandingReserve()));}
 @Test void separatesCurrenciesAndRemovesSettledExposure(){UUID id=UUID.randomUUID();projection.apply(event(id,1,"SGD","UNDER_REVIEW",1000,0));projection.apply(event(UUID.randomUUID(),1,"AUD","UNDER_REVIEW",2000,0));assertEquals(2,insights.dashboard(null).exposure().size());projection.apply(event(id,2,"SGD","SETTLED",1000,900));var d=insights.dashboard("SG");assertEquals(1,d.closedClaims());assertEquals(0,d.exposure().getFirst().outstandingReserve().signum());assertEquals(0,new BigDecimal("900").compareTo(d.exposure().getFirst().settledAmount()));assertEquals(0,new BigDecimal("2").compareTo(d.averageClosureHours()));assertNotNull(d.lastUpdated());}
 @Test void rejectsUnsupportedSchemaWithoutChangingProjection(){var original=event(UUID.randomUUID(),0,"SGD","SUBMITTED",0,0);var bad=new ClaimChanged(original.eventId(),99,original.claimId(),0,"TEST","SUBMITTED","SG","SGD",BigDecimal.ZERO,BigDecimal.ZERO,null,original.createdAt(),original.updatedAt(),null);assertThrows(IllegalArgumentException.class,()->projection.apply(bad));assertEquals(0,insights.dashboard(null).totalClaims());}
}
