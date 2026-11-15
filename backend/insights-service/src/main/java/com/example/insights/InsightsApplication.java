package com.example.insights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.insights", "com.example.platform"})
@EnableScheduling
public class InsightsApplication {
  public static void main(String[] args) {
    SpringApplication.run(InsightsApplication.class, args);
  }
}
