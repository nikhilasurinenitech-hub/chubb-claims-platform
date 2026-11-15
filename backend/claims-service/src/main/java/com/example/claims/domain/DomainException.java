package com.example.claims.domain;

import com.example.platform.BusinessRule;

public class DomainException extends BusinessRule {
  public DomainException(String code, String message) {
    super(code, message);
  }
}
