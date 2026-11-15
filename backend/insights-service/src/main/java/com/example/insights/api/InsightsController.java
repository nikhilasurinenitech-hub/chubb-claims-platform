package com.example.insights.api;

import com.example.insights.service.InsightsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insights")
@PreAuthorize("hasRole('STAFF')")
public class InsightsController {
  private final InsightsService service;

  public InsightsController(InsightsService service) {
    this.service = service;
  }

  @GetMapping
  public InsightsService.Dashboard dashboard(@RequestParam(required = false) String market) {
    return service.dashboard(market);
  }
}
