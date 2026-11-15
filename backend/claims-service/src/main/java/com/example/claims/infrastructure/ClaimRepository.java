package com.example.claims.infrastructure;
import com.example.claims.domain.*;
import com.example.claims.api.Contracts.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.Instant;
import java.util.*;
@Repository
public class ClaimRepository {
 private final JdbcTemplate jdbc;
 public ClaimRepository(JdbcTemplate jdbc) {this.jdbc=jdbc;}
 private static Instant instant(ResultSet r,String name)throws SQLException {var t=r.getTimestamp(name);return t==null?null:t.toInstant();}
 private final RowMapper<Claim> mapper=(r,n)->new Claim(r.getObject("id",UUID.class),r.getString("reference"),r.getString("claimant_id"),r.getString("type"),r.getString("market"),r.getString("currency"),r.getDate("incident_date").toLocalDate(),r.getString("location"),r.getString("description"),r.getBigDecimal("requested_amount"),ClaimStatus.valueOf(r.getString("status")),r.getString("assignee"),r.getBigDecimal("reserve_amount"),r.getBigDecimal("settlement_amount"),r.getLong("version"),instant(r,"created_at"),instant(r,"updated_at"),instant(r,"closed_at"));
 public Optional<Claim> find(UUID id) {return jdbc.query("select * from claim where id=?",mapper,id).stream().findFirst();}
 public void insert(Claim c) {jdbc.update("insert into claim(id,reference,claimant_id,type,market,currency,incident_date,location,description,requested_amount,status,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",c.id(),c.reference(),c.claimantId(),c.type(),c.market(),c.currency(),java.sql.Date.valueOf(c.incidentDate()),c.location(),c.description(),c.requestedAmount(),c.status().name(),Timestamp.from(c.createdAt()),Timestamp.from(c.updatedAt()));}
 public void update(Claim c,long oldVersion) {
  int count=jdbc.update("update claim set status=?,assignee=?,reserve_amount=?,settlement_amount=?,version=?,updated_at=?,closed_at=? where id=? and version=?",c.status().name(),c.assignee(),c.reserveAmount(),c.settlementAmount(),c.version(),Timestamp.from(c.updatedAt()),c.closedAt()==null?null:Timestamp.from(c.closedAt()),c.id(),oldVersion);
  if(count!=1) throw new DomainException("STALE_VERSION","Another user updated this claim. Refresh and retry.");
 }
 public com.example.claims.api.Contracts.Page list(String owner,String status,String market,String assignee,String search,int page,int size) {
  var sql=new StringBuilder(" from claim where 1=1");var args=new ArrayList<Object>();
  if(owner!=null){sql.append(" and claimant_id=?");args.add(owner);}
  if(status!=null&&!status.isBlank()){ClaimStatus.valueOf(status);sql.append(" and status=?");args.add(status);}
  if(market!=null&&!market.isBlank()){sql.append(" and market=?");args.add(market);}
  if(assignee!=null&&!assignee.isBlank()){sql.append(" and assignee=?");args.add(assignee);}
  if(search!=null&&!search.isBlank()){sql.append(" and (reference ilike ? or description ilike ?)");args.add("%"+search+"%");args.add("%"+search+"%");}
  long total=jdbc.queryForObject("select count(*)"+sql,Long.class,args.toArray());
  var paged=new ArrayList<>(args);paged.add(size);paged.add(page*size);
  return new com.example.claims.api.Contracts.Page(jdbc.query("select *"+sql+" order by updated_at desc,id limit ? offset ?",mapper,paged.toArray()),total,page,size);
 }
 public void history(Claim c,String actor,String action,String note) {jdbc.update("insert into claim_history(claim_id,actor,action,note,occurred_at) values (?,?,?,?,?)",c.id(),actor,action,note,Timestamp.from(c.updatedAt()));}
 public List<History> history(UUID id) {return jdbc.query("select * from claim_history where claim_id=? order by id",(r,n)->new History(r.getLong("id"),r.getString("actor"),r.getString("action"),r.getString("note"),instant(r,"occurred_at")),id);}
 public List<InformationRequest> requests(UUID id) {return jdbc.query("select * from information_request where claim_id=? order by requested_at,id",(r,n)->new InformationRequest(r.getObject("id",UUID.class),r.getString("question"),r.getString("requested_by"),instant(r,"requested_at"),r.getString("answer"),instant(r,"answered_at")),id);}
 public void request(UUID id,String question,String actor,Instant now) {jdbc.update("insert into information_request(id,claim_id,question,requested_by,requested_at) values (?,?,?,?,?)",UUID.randomUUID(),id,question,actor,Timestamp.from(now));}
 public void answer(UUID id,UUID request,String answer,Instant now) {if(jdbc.update("update information_request set answer=?,answered_at=? where id=? and claim_id=? and answered_at is null",answer,Timestamp.from(now),request,id)!=1)throw new DomainException("INVALID_REQUEST","That request is missing or already answered");}
 public void lockSubmission(String owner,String key) {jdbc.queryForList("select pg_advisory_xact_lock(hashtextextended(?,0))",owner+":"+key);}
 public List<Map<String,Object>> receipt(String owner,String key) {return jdbc.queryForList("select request_hash,claim_id from submission_receipt where claimant_id=? and idempotency_key=?",owner,key);}
 public void receipt(String owner,String key,String hash,UUID id) {jdbc.update("insert into submission_receipt values (?,?,?,?)",owner,key,hash,id);}
 public void outbox(UUID event,Claim c,String payload) {jdbc.update("insert into outbox_event(id,aggregate_id,aggregate_version,payload,created_at) values (?,?,?,?,?)",event,c.id(),c.version(),payload,Timestamp.from(c.updatedAt()));}
 public long pendingEvents() {return jdbc.queryForObject("select count(*) from outbox_event where published_at is null",Long.class);}
}
