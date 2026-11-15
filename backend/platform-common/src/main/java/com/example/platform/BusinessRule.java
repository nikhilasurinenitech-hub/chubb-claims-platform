package com.example.platform;

public class BusinessRule extends RuntimeException {
  private final String code;

  public BusinessRule(String code, String message) {
    super(message);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
