package com.example.notification.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Configuration
public class SecurityConfig {

  @Value("${app.jwt.secret}") String secret;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable())
        .authorizeHttpRequests(auth->auth
            .requestMatchers(HttpMethod.GET, "/api/channels").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(new JwtFilter(secret), BasicAuthenticationFilter.class);
    return http.build();
  }

  static class JwtFilter extends BasicAuthenticationFilter {
    private final SecretKey key;
    public JwtFilter(String secret){ super(authentication->authentication); this.key = Keys.hmacShaKeyFor(secret.getBytes()); }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
      var auth = request.getHeader("Authorization");
      if(auth!=null && auth.startsWith("Bearer ")){
        try{
          Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(auth.substring(7)).getBody();
          SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject(), null, java.util.List.of()));
        }catch(Exception ignored){}
      }
      chain.doFilter(request, response);
    }
  }
}
