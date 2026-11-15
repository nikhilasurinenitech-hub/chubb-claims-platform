package com.example.insights.service;
import com.example.platform.ClaimChanged;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.*;
@Service
public class ProjectionService {
 private final JdbcTemplate jdbc;
 public ProjectionService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @Transactional public int apply(ClaimChanged e) {
  if(e.schemaVersion()!=1||e.claimId()==null||e.eventId()==null||e.version()<0||e.createdAt()==null||e.updatedAt()==null||!Set.of("SUBMITTED","UNDER_REVIEW","AWAITING_INFORMATION","APPROVED","SETTLED","REJECTED").contains(e.status())||e.reserveAmount()==null||e.reserveAmount().signum()<0||e.settlementAmount()==null||e.settlementAmount().signum()<0)throw new IllegalArgumentException("Invalid claim event");
  Currency.getInstance(e.currency());
  return jdbc.update("""
   insert into claim_projection(claim_id,aggregate_version,reference,status,market,currency,reserve_amount,settlement_amount,assignee,created_at,updated_at,closed_at)
   values (?,?,?,?,?,?,?,?,?,?,?,?) on conflict(claim_id) do update set
   aggregate_version=excluded.aggregate_version,reference=excluded.reference,status=excluded.status,market=excluded.market,currency=excluded.currency,
   reserve_amount=excluded.reserve_amount,settlement_amount=excluded.settlement_amount,assignee=excluded.assignee,
   created_at=excluded.created_at,updated_at=excluded.updated_at,closed_at=excluded.closed_at,projected_at=CURRENT_TIMESTAMP
   where excluded.aggregate_version > claim_projection.aggregate_version
   """,e.claimId(),e.version(),e.reference(),e.status(),e.market(),e.currency(),e.reserveAmount(),e.settlementAmount(),e.assignee(),Timestamp.from(e.createdAt()),Timestamp.from(e.updatedAt()),e.closedAt()==null?null:Timestamp.from(e.closedAt()));
 }
}
