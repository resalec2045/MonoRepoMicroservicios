package com.example.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {
  private final SecretKey key;

  public JwtService(@Value("${app.jwt.secret}") String secret){
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String createToken(String subject){
    long now = System.currentTimeMillis();
    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 1000L*60*60*6))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
  }
}
