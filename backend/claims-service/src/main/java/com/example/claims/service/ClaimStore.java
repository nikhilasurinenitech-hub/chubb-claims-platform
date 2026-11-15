package com.example.claims.service;

import com.example.claims.domain.Claim;
import com.example.claims.service.Contracts.*;
import java.time.Instant;
import java.util.*;

/** Application port. Implemented by the PostgreSQL adapter. */
public interface ClaimStore {
  Optional<Claim> find(UUID id);

  void insert(Claim claim);

  void update(Claim claim, long oldVersion);

  Contracts.Page list(
      String owner,
      String status,
      String market,
      String assignee,
      String search,
      int page,
      int size);

  void history(Claim claim, String actor, String action, String note);

  List<History> history(UUID id);

  List<InformationRequest> requests(UUID id);

  void request(UUID id, String question, String actor, Instant now);

  void answer(UUID id, UUID request, String answer, Instant now);

  void lockSubmission(String owner, String key);

  Optional<SubmissionReceipt> receipt(String owner, String key);

  void receipt(String owner, String key, String hash, UUID id);

  void outbox(UUID event, Claim claim, String payload);

  long pendingEvents();
}
