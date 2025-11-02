package com.example.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="notifications")
public class Notification {
  @Id
  private String id = UUID.randomUUID().toString();
  private String recipientEmail;
  private String recipientPhone;
  private String channel; // email, sms, whatsapp
  @Column(length=4096)
  private String content;
  private String status; // PENDING, SENT, FAILED, SCHEDULED
  private Instant createdAt = Instant.now();
  private Instant scheduledAt;
  private String errorMessage;

  // getters and setters
  public String getId(){return id;}
  public String getRecipientEmail(){return recipientEmail;}
  public void setRecipientEmail(String v){this.recipientEmail=v;}
  public String getRecipientPhone(){return recipientPhone;}
  public void setRecipientPhone(String v){this.recipientPhone=v;}
  public String getChannel(){return channel;}
  public void setChannel(String v){this.channel=v;}
  public String getContent(){return content;}
  public void setContent(String v){this.content=v;}
  public String getStatus(){return status;}
  public void setStatus(String v){this.status=v;}
  public Instant getCreatedAt(){return createdAt;}
  public void setCreatedAt(Instant i){this.createdAt=i;}
  public Instant getScheduledAt(){return scheduledAt;}
  public void setScheduledAt(Instant i){this.scheduledAt=i;}
  public String getErrorMessage(){return errorMessage;}
  public void setErrorMessage(String e){this.errorMessage=e;}
}
