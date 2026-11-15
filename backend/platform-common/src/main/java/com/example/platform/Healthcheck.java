package com.example.platform;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public final class Healthcheck {
  private Healthcheck() {}

  public static void main(String[] args) throws Exception {
    var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    var request =
        HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + args[0] + "/actuator/health/readiness"))
            .timeout(Duration.ofSeconds(3))
            .GET()
            .build();
    if (client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() != 200)
      System.exit(1);
  }
}
