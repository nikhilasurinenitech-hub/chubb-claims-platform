package com.example.claims.service;
import java.util.List;
import com.example.claims.api.Contracts.Market;
import com.example.claims.domain.DomainException;
public final class Markets {
 private Markets() {}
 // Immutable reference data is cached for the lifetime of this process.
 public static final List<Market> ALL=List.of(new Market("SG","Singapore","SGD"),new Market("AU","Australia","AUD"),new Market("HK","Hong Kong","HKD"),new Market("JP","Japan","JPY"),new Market("MY","Malaysia","MYR"),new Market("NZ","New Zealand","NZD"));
 public static Market get(String code) {return ALL.stream().filter(m->m.code().equals(code)).findFirst().orElseThrow(()->new DomainException("INVALID_MARKET","Choose a supported market"));}
}
