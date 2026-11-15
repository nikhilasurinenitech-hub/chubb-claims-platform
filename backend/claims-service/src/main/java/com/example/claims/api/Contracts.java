package com.example.claims.api;
import com.example.claims.domain.Claim;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
public final class Contracts {
 private Contracts() {}
 public record CreateClaim(@NotBlank @Pattern(regexp="MOTOR|PROPERTY") String type,@NotBlank String market,
  @NotNull @PastOrPresent LocalDate incidentDate,@NotBlank @Size(max=200) String location,
  @NotBlank @Size(min=10,max=4000) String description,@NotNull @DecimalMin("0.01") @DecimalMax("999999999999.99") BigDecimal requestedAmount) {}
 public record Command(@NotNull @Min(0) Long version,@Size(max=2000) String note,BigDecimal amount) {}
 public record Response(@NotNull @Min(0) Long version,@NotBlank @Size(min=2,max=4000) String answer) {}
 public record History(long id,String actor,String action,String note,Instant occurredAt) {}
 public record InformationRequest(UUID id,String question,String requestedBy,Instant requestedAt,String answer,Instant answeredAt) {}
 public record Detail(Claim claim,List<History> history,List<InformationRequest> informationRequests) {}
 public record Page(List<Claim> items,long total,int page,int size) {}
 public record Created(Detail detail,boolean replayed) {}
 public record Market(String code,String name,String currency) {}
}
