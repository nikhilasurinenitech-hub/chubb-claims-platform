package com.example.platform;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClaimChanged(
    String eventId,
    int schemaVersion,
    UUID claimId,
    long version,
    String reference,
    String status,
    String market,
    String currency,
    BigDecimal reserveAmount,
    BigDecimal settlementAmount,
    String assignee,
    Instant createdAt,
    Instant updatedAt,
    Instant closedAt) {}
