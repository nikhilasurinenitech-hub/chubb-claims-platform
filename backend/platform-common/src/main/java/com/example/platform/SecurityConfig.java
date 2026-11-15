package com.example.platform;
import java.time.Clock;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean Clock clock() { return Clock.systemUTC(); }
 @Bean PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }
 @Bean UserDetailsService users(@Value("${demo.password:ClaimsDemo!2026}") String password, PasswordEncoder encoder) {
  return new InMemoryUserDetailsManager(
   User.withUsername("maya").password(encoder.encode(password)).roles("CLAIMANT").build(),
   User.withUsername("leo").password(encoder.encode(password)).roles("CLAIMANT").build(),
   User.withUsername("alex").password(encoder.encode(password)).roles("STAFF").build(),
   User.withUsername("priya").password(encoder.encode(password)).roles("STAFF").build());
 }
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  return http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health", "/actuator/health/**").permitAll().anyRequest().authenticated())
   .httpBasic(b -> b.authenticationEntryPoint((req,res,e) -> {res.setStatus(401);res.setContentType("application/problem+json");res.getWriter().write("{\"title\":\"Authentication required\",\"status\":401,\"detail\":\"Sign in with a demo account\",\"code\":\"UNAUTHENTICATED\"}");}))
   .exceptionHandling(e -> e.accessDeniedHandler((req,res,x) -> {res.setStatus(403);res.setContentType("application/problem+json");res.getWriter().write("{\"title\":\"Forbidden\",\"status\":403,\"detail\":\"Your role cannot perform this action\",\"code\":\"FORBIDDEN\"}");})).build();
 }
 @RestController static class SessionController {
  @GetMapping("/api/session") Map<String,String> session(Authentication auth) {
   return Map.of("username", auth.getName(), "role", auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
  }
 }
}
