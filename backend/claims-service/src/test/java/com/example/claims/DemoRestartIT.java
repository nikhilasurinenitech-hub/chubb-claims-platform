package com.example.claims;

import static org.junit.jupiter.api.Assertions.*;

import com.example.claims.infrastructure.DemoSeeder;
import com.example.claims.service.ClaimService;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "outbox.enabled=false",
      "spring.kafka.admin.auto-create=false",
      "demo.seed=false"
    })
class DemoRestartIT {
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

  @DynamicPropertySource
  static void db(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "&currentSchema=claims");
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
  }

  static class MutableClock extends Clock {
    volatile Instant current = Instant.parse("2026-11-15T12:00:00Z");

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return Clock.fixed(current, zone);
    }

    @Override
    public Instant instant() {
      return current;
    }
  }

  @TestConfiguration(proxyBeanMethods = false)
  static class TimeConfig {
    @Bean
    @Primary
    MutableClock testClock() {
      return new MutableClock();
    }
  }

  @Autowired ClaimService service;
  @Autowired MutableClock clock;
  @Autowired JdbcTemplate jdbc;

  @Test
  void replayingDemoFixturesOnALaterDayDoesNotChangeTheirPayload() {
    var seeder = new DemoSeeder(service);
    seeder.seed();
    var before = jdbc.queryForList("select id,status,version from claim order by id");
    assertEquals(8, before.size());
    assertEquals(
        LocalDate.of(2026, 11, 12),
        jdbc.queryForObject(
                "select incident_date from claim c join submission_receipt r on c.id=r.claim_id"
                    + " where r.idempotency_key='demo-seed-v1-1'",
                java.sql.Date.class)
            .toLocalDate());
    clock.current = Instant.parse("2026-11-16T12:00:00Z");
    assertDoesNotThrow(seeder::seed);
    assertEquals(before, jdbc.queryForList("select id,status,version from claim order by id"));
    assertEquals(8, jdbc.queryForObject("select count(*) from submission_receipt", Integer.class));
  }
}
