package com.example.insights.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsightsService {
  public record Officer(String username, long assigned, long open, long waiting, long closed) {}

  public record Exposure(
      String currency,
      BigDecimal outstandingReserve,
      BigDecimal settledAmount,
      long unassessedClaims) {}

  public record Dashboard(
      long totalClaims,
      long openClaims,
      long closedClaims,
      BigDecimal averageClosureHours,
      Map<String, Long> byStatus,
      List<Officer> officers,
      List<Exposure> exposure,
      Instant lastUpdated) {}

  private final JdbcTemplate jdbc;

  public InsightsService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Transactional(
      readOnly = true,
      isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
  public Dashboard dashboard(String market) {
    if (market != null && !market.matches("SG|AU|HK|JP|MY|NZ"))
      throw new IllegalArgumentException("Invalid market");
    String where = market == null ? "" : " where market=?";
    Object[] args = market == null ? new Object[] {} : new Object[] {market};
    Map<String, Long> statuses = new LinkedHashMap<>();
    for (String s :
        List.of(
            "SUBMITTED", "UNDER_REVIEW", "AWAITING_INFORMATION", "APPROVED", "SETTLED", "REJECTED"))
      statuses.put(s, 0L);
    jdbc.query(
        "select status,count(*) n from claim_projection" + where + " group by status",
        (org.springframework.jdbc.core.RowCallbackHandler)
            r -> statuses.put(r.getString("status"), r.getLong("n")),
        args);
    long total = statuses.values().stream().mapToLong(Long::longValue).sum(),
        closed = statuses.get("SETTLED") + statuses.get("REJECTED");
    var officers =
        jdbc.query(
            "select coalesce(assignee,'Unassigned') officer,count(*) assigned,count(*) filter(where"
                + " status not in ('SETTLED','REJECTED')) open,count(*) filter(where"
                + " status='AWAITING_INFORMATION') waiting,count(*) filter(where status in"
                + " ('SETTLED','REJECTED')) closed from claim_projection"
                + where
                + " group by assignee order by officer",
            (r, n) ->
                new Officer(
                    r.getString("officer"),
                    r.getLong("assigned"),
                    r.getLong("open"),
                    r.getLong("waiting"),
                    r.getLong("closed")),
            args);
    var exposure =
        jdbc.query(
            "select currency,coalesce(sum(reserve_amount) filter(where status not in"
                + " ('SETTLED','REJECTED')),0) outstanding,coalesce(sum(settlement_amount)"
                + " filter(where status='SETTLED'),0) paid,count(*) filter(where status not in"
                + " ('SETTLED','REJECTED') and reserve_amount=0) unassessed from claim_projection"
                + where
                + " group by currency order by currency",
            (r, n) ->
                new Exposure(
                    r.getString("currency"),
                    r.getBigDecimal("outstanding"),
                    r.getBigDecimal("paid"),
                    r.getLong("unassessed")),
            args);
    var average =
        jdbc.queryForObject(
            "select coalesce(avg(extract(epoch from(closed_at-created_at))/3600.0),0) from"
                + " claim_projection"
                + where,
            BigDecimal.class,
            args);
    var latest =
        jdbc.queryForObject(
            "select max(projected_at) from claim_projection" + where, Timestamp.class, args);
    return new Dashboard(
        total,
        total - closed,
        closed,
        average,
        statuses,
        officers,
        exposure,
        latest == null ? null : latest.toInstant());
  }
}
