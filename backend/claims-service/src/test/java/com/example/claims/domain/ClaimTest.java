package com.example.claims.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClaimTest {
  final Instant now = Instant.parse("2026-11-15T12:00:00Z");

  Claim submitted() {
    return Claim.submit(
        UUID.randomUUID(),
        "CLM-TEST",
        "maya",
        "MOTOR",
        "SG",
        "SGD",
        LocalDate.of(2026, 11, 10),
        "Singapore",
        "Parked vehicle damaged",
        new BigDecimal("5000.00"),
        now);
  }

  @Test
  void fullInformationAndSettlementFlow() {
    var c =
        submitted()
            .assign("alex", now)
            .requestInformation("alex", now)
            .respond("maya", now)
            .assess("alex", new BigDecimal("4000"), now)
            .approve("alex", now)
            .settle("alex", new BigDecimal("3800"), now);
    assertEquals(ClaimStatus.SETTLED, c.status());
    assertEquals(new BigDecimal("3800"), c.settlementAmount());
    assertEquals(now, c.closedAt());
    assertEquals(6, c.version());
  }

  @Test
  void preventsDoubleAssignment() {
    var c = submitted().assign("alex", now);
    assertThrows(DomainException.class, () -> c.assign("priya", now));
  }

  @Test
  void enforcesAssignedOfficer() {
    var c = submitted().assign("alex", now);
    assertThrows(DomainException.class, () -> c.assess("priya", BigDecimal.TEN, now));
  }

  @Test
  void enforcesClaimantOwnershipWhenResponding() {
    var c = submitted().assign("alex", now).requestInformation("alex", now);
    assertThrows(DomainException.class, () -> c.respond("leo", now));
  }

  @Test
  void cannotApproveBeforeAssessment() {
    var c = submitted().assign("alex", now);
    assertThrows(DomainException.class, () -> c.approve("alex", now));
  }

  @Test
  void cannotSettleBeforeApproval() {
    var c = submitted().assign("alex", now);
    assertThrows(DomainException.class, () -> c.settle("alex", BigDecimal.TEN, now));
  }

  @Test
  void settlementCannotExceedReserve() {
    var c =
        submitted()
            .assign("alex", now)
            .assess("alex", new BigDecimal("100"), now)
            .approve("alex", now);
    assertThrows(DomainException.class, () -> c.settle("alex", new BigDecimal("101"), now));
  }

  @Test
  void finalClaimsCannotBeProgressed() {
    var c = submitted().assign("alex", now).reject("alex", now);
    assertThrows(DomainException.class, () -> c.requestInformation("alex", now));
  }

  @Test
  void moneyMustBePositiveAndFitCurrencyPrecision() {
    assertThrows(
        DomainException.class,
        () -> submitted().assign("alex", now).assess("alex", new BigDecimal("1.001"), now));
    assertThrows(
        DomainException.class,
        () -> submitted().assign("alex", now).assess("alex", BigDecimal.ZERO, now));
  }

  @Test
  void waitingClaimMustBeAnsweredBeforeAssessment() {
    var c = submitted().assign("alex", now).requestInformation("alex", now);
    assertThrows(DomainException.class, () -> c.assess("alex", BigDecimal.TEN, now));
  }
}
