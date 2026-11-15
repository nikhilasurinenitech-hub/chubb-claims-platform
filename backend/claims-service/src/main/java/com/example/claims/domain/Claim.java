package com.example.claims.domain;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public record Claim(
    UUID id,
    String reference,
    String claimantId,
    String type,
    String market,
    String currency,
    LocalDate incidentDate,
    String location,
    String description,
    BigDecimal requestedAmount,
    ClaimStatus status,
    String assignee,
    BigDecimal reserveAmount,
    BigDecimal settlementAmount,
    long version,
    Instant createdAt,
    Instant updatedAt,
    Instant closedAt) {
  public static Claim submit(
      UUID id,
      String ref,
      String owner,
      String type,
      String market,
      String currency,
      LocalDate date,
      String location,
      String description,
      BigDecimal requested,
      Instant now) {
    money(requested, currency);
    if (!Set.of("MOTOR", "PROPERTY").contains(type))
      fail("INVALID_TYPE", "Choose motor or property");
    if (date.isAfter(LocalDate.ofInstant(now, ZoneOffset.UTC)))
      fail("INVALID_DATE", "Incident date cannot be in the future");
    return new Claim(
        id,
        ref,
        owner,
        type,
        market,
        currency,
        date,
        location,
        description,
        requested,
        ClaimStatus.SUBMITTED,
        null,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        0,
        now,
        now,
        null);
  }

  public Claim assign(String officer, Instant now) {
    require(ClaimStatus.SUBMITTED);
    if (assignee != null) fail("ALREADY_ASSIGNED", "Another officer has picked up this claim");
    return next(ClaimStatus.UNDER_REVIEW, officer, reserveAmount, settlementAmount, now, null);
  }

  public Claim requestInformation(String officer, Instant now) {
    officer(officer);
    require(ClaimStatus.UNDER_REVIEW);
    return next(
        ClaimStatus.AWAITING_INFORMATION, assignee, reserveAmount, settlementAmount, now, null);
  }

  public Claim respond(String owner, Instant now) {
    if (!claimantId.equals(owner)) fail("FORBIDDEN", "Only the claimant can answer");
    require(ClaimStatus.AWAITING_INFORMATION);
    return next(ClaimStatus.UNDER_REVIEW, assignee, reserveAmount, settlementAmount, now, null);
  }

  public Claim assess(String officer, BigDecimal amount, Instant now) {
    officer(officer);
    require(ClaimStatus.UNDER_REVIEW);
    money(amount, currency);
    return next(status, assignee, amount, settlementAmount, now, null);
  }

  public Claim approve(String officer, Instant now) {
    officer(officer);
    require(ClaimStatus.UNDER_REVIEW);
    money(reserveAmount, currency);
    return next(ClaimStatus.APPROVED, assignee, reserveAmount, settlementAmount, now, null);
  }

  public Claim settle(String officer, BigDecimal amount, Instant now) {
    officer(officer);
    require(ClaimStatus.APPROVED);
    money(amount, currency);
    if (amount.compareTo(reserveAmount) > 0)
      fail("INVALID_AMOUNT", "Settlement cannot exceed the assessed reserve");
    return next(ClaimStatus.SETTLED, assignee, reserveAmount, amount, now, now);
  }

  public Claim reject(String officer, Instant now) {
    officer(officer);
    require(ClaimStatus.UNDER_REVIEW);
    return next(ClaimStatus.REJECTED, assignee, BigDecimal.ZERO, BigDecimal.ZERO, now, now);
  }

  private Claim next(
      ClaimStatus s, String a, BigDecimal r, BigDecimal paid, Instant now, Instant closed) {
    return new Claim(
        id,
        reference,
        claimantId,
        type,
        market,
        currency,
        incidentDate,
        location,
        description,
        requestedAmount,
        s,
        a,
        r,
        paid,
        version + 1,
        createdAt,
        now,
        closed);
  }

  private void officer(String actor) {
    if (!Objects.equals(assignee, actor))
      fail("FORBIDDEN", "Only the assigned officer may progress this claim");
  }

  private void require(ClaimStatus expected) {
    if (status != expected)
      fail(
          "INVALID_TRANSITION",
          "This action requires " + expected + "; current status is " + status);
  }

  private static void money(BigDecimal amount, String currency) {
    int digits = Currency.getInstance(currency).getDefaultFractionDigits();
    if (amount == null
        || amount.signum() <= 0
        || amount.compareTo(new BigDecimal("999999999999.99")) > 0
        || amount.stripTrailingZeros().scale() > digits)
      fail("INVALID_AMOUNT", "Enter a positive amount with valid currency precision");
  }

  private static void fail(String code, String message) {
    throw new DomainException(code, message);
  }
}
