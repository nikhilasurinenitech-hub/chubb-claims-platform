package com.example.claims.api;

import com.example.claims.service.*;
import com.example.claims.service.Contracts.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClaimController {
  private final ClaimService service;

  public ClaimController(ClaimService service) {
    this.service = service;
  }

  private boolean staff(Authentication a) {
    return a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_STAFF"));
  }

  @GetMapping("/markets")
  public List<Market> markets() {
    return Markets.ALL;
  }

  @GetMapping("/claims")
  public Page list(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String market,
      @RequestParam(required = false) String assignee,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      Authentication auth) {
    return service.list(auth.getName(), staff(auth), status, market, assignee, search, page, size);
  }

  @PostMapping("/claims")
  @PreAuthorize("hasRole('CLAIMANT')")
  public ResponseEntity<Detail> create(
      @Valid @RequestBody CreateClaim request,
      @RequestHeader("Idempotency-Key") String key,
      Authentication auth) {
    var result = service.create(request, key, auth.getName());
    return ResponseEntity.status(result.replayed() ? 200 : 201)
        .location(URI.create("/api/claims/" + result.detail().claim().id()))
        .header("Idempotent-Replay", Boolean.toString(result.replayed()))
        .body(result.detail());
  }

  @GetMapping("/claims/{id}")
  public Detail detail(@PathVariable UUID id, Authentication auth) {
    return service.detail(id, auth.getName(), staff(auth));
  }

  @PostMapping(
      "/claims/{id}/{operation:assign|information-requests|assessment|approve|settle|reject}")
  @PreAuthorize("hasRole('STAFF')")
  public Detail act(
      @PathVariable UUID id,
      @PathVariable String operation,
      @Valid @RequestBody Command command,
      Authentication auth) {
    return service.act(id, operation, command, auth.getName());
  }

  @PostMapping("/claims/{id}/information-requests/{requestId}/response")
  @PreAuthorize("hasRole('CLAIMANT')")
  public Detail respond(
      @PathVariable UUID id,
      @PathVariable UUID requestId,
      @Valid @RequestBody Response response,
      Authentication auth) {
    return service.respond(id, requestId, response, auth.getName());
  }

  @GetMapping("/operations")
  @PreAuthorize("hasRole('STAFF')")
  public Map<String, Long> operations() {
    return Map.of("pendingEvents", service.pendingEvents());
  }
}
