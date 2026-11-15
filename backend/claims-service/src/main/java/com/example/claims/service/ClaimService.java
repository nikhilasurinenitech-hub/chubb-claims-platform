package com.example.claims.service;
import com.example.claims.domain.*;
import com.example.claims.api.Contracts.*;
import com.example.claims.infrastructure.ClaimRepository;
import com.example.platform.ClaimChanged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.*;
@Service
public class ClaimService {
 private final ClaimRepository repo;private final ObjectMapper json;private final Clock clock;
 public ClaimService(ClaimRepository repo,ObjectMapper json,Clock clock){this.repo=repo;this.json=json;this.clock=clock;}
 @Transactional public Created create(CreateClaim request,String key,String actor) {
  if(key==null||!key.matches("[A-Za-z0-9._:-]{8,100}"))throw new DomainException("INVALID_IDEMPOTENCY_KEY","Provide an Idempotency-Key of 8–100 letters, digits or ._:-");
  var market=Markets.get(request.market());repo.lockSubmission(actor,key);String hash=fingerprint(request);
  var receipts=repo.receipt(actor,key);
  if(!receipts.isEmpty()) {var receipt=receipts.getFirst();if(!hash.equals(receipt.get("request_hash")))throw new DomainException("IDEMPOTENCY_CONFLICT","This submission key was used with different details");return new Created(detail((UUID)receipt.get("claim_id"),actor,false),true);}
  UUID id=UUID.randomUUID();var c=Claim.submit(id,"CLM-"+id.toString().substring(0,13).replace("-","").toUpperCase(),actor,request.type(),market.code(),market.currency(),request.incidentDate(),request.location().strip(),request.description().strip(),request.requestedAmount(),clock.instant());
  repo.insert(c);repo.receipt(actor,key,hash,id);record(c,actor,"SUBMITTED","Claim submitted");return new Created(detail(id,actor,false),false);
 }
 @Transactional(readOnly=true) public Detail detail(UUID id,String actor,boolean staff) {var c=visible(id,actor,staff);return new Detail(c,repo.history(id),repo.requests(id));}
 private Claim visible(UUID id,String actor,boolean staff) {var c=repo.find(id).orElseThrow(()->new DomainException("NOT_FOUND","Claim not found"));if(!staff&&!c.claimantId().equals(actor))throw new DomainException("NOT_FOUND","Claim not found");return c;}
 @Transactional public Detail act(UUID id,String operation,Command command,String actor) {
  var c=visible(id,actor,true);checkVersion(c,command.version());var now=clock.instant();String note=command.note()==null?"":command.note().strip();
  var next=switch(operation) {
   case "assign" -> c.assign(actor,now);
   case "information-requests" -> {requireNote(note,"Describe the information needed");yield c.requestInformation(actor,now);}
   case "assessment" -> {requireNote(note,"Record your assessment rationale");yield c.assess(actor,command.amount(),now);}
   case "approve" -> c.approve(actor,now);
   case "settle" -> c.settle(actor,command.amount(),now);
   case "reject" -> {requireNote(note,"Explain why this claim was rejected");yield c.reject(actor,now);}
   default -> throw new DomainException("NOT_FOUND","Unknown operation");
  };
  repo.update(next,c.version());
  if(operation.equals("information-requests")) repo.request(id,note,actor,now);
  record(next,actor,operation.toUpperCase().replace('-','_'),note.isBlank()?operation+" completed":note);
  return detail(id,actor,true);
 }
 @Transactional public Detail respond(UUID id,UUID request,Response command,String actor) {
  var c=visible(id,actor,false);checkVersion(c,command.version());var next=c.respond(actor,clock.instant());repo.update(next,c.version());repo.answer(id,request,command.answer().strip(),clock.instant());record(next,actor,"INFORMATION_PROVIDED",command.answer().strip());return detail(id,actor,false);
 }
 private void checkVersion(Claim c,long version) {if(c.version()!=version)throw new DomainException("STALE_VERSION","This claim changed. Refresh before trying again.");}
 private void requireNote(String note,String message) {if(note.length()<5)throw new DomainException("NOTE_REQUIRED",message+" (at least 5 characters)");}
 private String fingerprint(CreateClaim request) {try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(json.writeValueAsBytes(request)));}catch(Exception e){throw new IllegalStateException(e);}}
 private void record(Claim c,String actor,String action,String note) {
  repo.history(c,actor,action,note);UUID event=UUID.randomUUID();
  var message=new ClaimChanged(event.toString(),1,c.id(),c.version(),c.reference(),c.status().name(),c.market(),c.currency(),c.reserveAmount(),c.settlementAmount(),c.assignee(),c.createdAt(),c.updatedAt(),c.closedAt());
  try {repo.outbox(event,c,json.writeValueAsString(message));}catch(Exception e){throw new IllegalStateException("Could not encode event",e);}
 }
}
