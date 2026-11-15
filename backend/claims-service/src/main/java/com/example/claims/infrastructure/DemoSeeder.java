package com.example.claims.infrastructure;

import com.example.claims.service.ClaimService;
import com.example.claims.service.Contracts.*;
import java.math.BigDecimal;
import java.time.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.seed", havingValue = "true")
public class DemoSeeder {
  private final ClaimService service;

  public DemoSeeder(ClaimService service) {
    this.service = service;
  }

  @EventListener(ApplicationReadyEvent.class)
  @org.springframework.transaction.annotation.Transactional
  public void seed() {
    seed(
        1,
        "maya",
        "MOTOR",
        "SG",
        "Singapore",
        "Rear bumper damaged in a low-speed parking incident",
        4200,
        "SUBMITTED");
    seed(
        2,
        "leo",
        "PROPERTY",
        "AU",
        "Melbourne",
        "Storm water damaged the lounge ceiling and flooring",
        12500,
        "UNDER_REVIEW");
    seed(
        3,
        "maya",
        "MOTOR",
        "HK",
        "Kowloon",
        "Parked vehicle door and mirror damaged by another vehicle",
        8500,
        "AWAITING_INFORMATION");
    seed(
        4,
        "leo",
        "PROPERTY",
        "JP",
        "Osaka",
        "Kitchen water leak damaged fitted cabinets and flooring",
        280000,
        "APPROVED");
    seed(
        5,
        "maya",
        "MOTOR",
        "MY",
        "Kuala Lumpur",
        "Windshield cracked after a road debris impact",
        2200,
        "SETTLED");
    seed(
        6,
        "leo",
        "PROPERTY",
        "NZ",
        "Auckland",
        "Fence damage reported after overnight high winds",
        3600,
        "REJECTED");
    seed(
        7,
        "maya",
        "PROPERTY",
        "SG",
        "Singapore",
        "Water leak damaged a bedroom wall and fitted wardrobe",
        6800,
        "UNDER_REVIEW");
    seed(
        8,
        "leo",
        "MOTOR",
        "AU",
        "Sydney",
        "Front panel damaged during a minor intersection collision",
        5700,
        "SUBMITTED");
  }

  private void seed(
      int i,
      String owner,
      String type,
      String market,
      String location,
      String description,
      int amount,
      String target) {
    // Versioned fixtures must keep the same payload when replayed on later days.
    var result =
        service.create(
            new CreateClaim(
                type,
                market,
                LocalDate.of(2026, 11, 15).minusDays(i + 2),
                location,
                description,
                BigDecimal.valueOf(amount)),
            "demo-seed-v1-" + i,
            owner);
    if (result.replayed() || target.equals("SUBMITTED")) return;
    String actor = i % 2 == 0 ? "priya" : "alex";
    var d =
        service.act(
            result.detail().claim().id(),
            "assign",
            new Command(0L, "Picked up for review", null),
            actor);
    if (target.equals("AWAITING_INFORMATION")) {
      service.act(
          d.claim().id(),
          "information-requests",
          new Command(
              d.claim().version(),
              "Please provide a repair estimate and details of the damage.",
              null),
          actor);
      return;
    }
    if (target.equals("REJECTED")) {
      service.act(
          d.claim().id(),
          "reject",
          new Command(
              d.claim().version(),
              "Demo assessment: reported damage falls outside the assumed cover.",
              null),
          actor);
      return;
    }
    d =
        service.act(
            d.claim().id(),
            "assessment",
            new Command(
                d.claim().version(),
                "Demo repair estimate reviewed; reserve recorded.",
                BigDecimal.valueOf(amount)),
            actor);
    if (target.equals("UNDER_REVIEW")) return;
    d =
        service.act(
            d.claim().id(),
            "approve",
            new Command(d.claim().version(), "Assessment approved", null),
            actor);
    if (target.equals("SETTLED"))
      service.act(
          d.claim().id(),
          "settle",
          new Command(
              d.claim().version(), "Demo settlement decision recorded", BigDecimal.valueOf(amount)),
          actor);
  }
}
