package com.example.auth.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private String passwordHash;
  private String phone;
  private Instant createdAt = Instant.now();
  private boolean emailConfirmed;

  // getters and setters
  public Long getId(){return id;}
  public void setId(Long id){this.id=id;}
  public String getEmail(){return email;}
  public void setEmail(String email){this.email=email;}
  public String getPasswordHash(){return passwordHash;}
  public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
  public String getPhone(){return phone;}
  public void setPhone(String phone){this.phone=phone;}
  public Instant getCreatedAt(){return createdAt;}
  public void setCreatedAt(Instant t){this.createdAt=t;}
  public boolean isEmailConfirmed(){return emailConfirmed;}
  public void setEmailConfirmed(boolean e){this.emailConfirmed=e;}
}
