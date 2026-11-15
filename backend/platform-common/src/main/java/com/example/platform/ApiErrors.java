package com.example.platform;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiErrors {
 private ProblemDetail problem(int status, String code, String detail) {
  var p = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detail);
  p.setType(URI.create("urn:claims:error:" + code.toLowerCase().replace('_','-'))); p.setProperty("code",code); return p;
 }
 @ExceptionHandler(BusinessRule.class) ProblemDetail rule(BusinessRule e) {
  int status = switch(e.code()) {case "FORBIDDEN" -> 403; case "NOT_FOUND" -> 404; case "STALE_VERSION", "INVALID_TRANSITION", "ALREADY_ASSIGNED", "IDEMPOTENCY_CONFLICT" -> 409; default -> 400;};
  return problem(status, e.code(), e.getMessage());
 }
 @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail validation(MethodArgumentNotValidException e) {
  var p = problem(400,"VALIDATION_FAILED","Check the highlighted fields");
  p.setProperty("errors",e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(x->x.getField(),x->x.getDefaultMessage(),(a,b)->a))); return p;
 }
 @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class}) ProblemDetail malformed(Exception e) {return problem(400,"INVALID_REQUEST","The request contains an invalid value");}
 @ExceptionHandler(AccessDeniedException.class) ProblemDetail denied(Exception e) {return problem(403,"FORBIDDEN","You cannot access this resource");}
 @ExceptionHandler(DataIntegrityViolationException.class) ProblemDetail conflict(Exception e) {return problem(409,"DATA_CONFLICT","This operation conflicts with an existing record. Refresh and retry.");}
 @ExceptionHandler(Exception.class) ProblemDetail unexpected(Exception e) {
  String id=java.util.UUID.randomUUID().toString(); LoggerFactory.getLogger(ApiErrors.class).error("Unhandled error {}", id,e);
  var p=problem(500,"INTERNAL_ERROR","An unexpected error occurred. Please retry.");p.setProperty("traceId",id);return p;
 }
}
