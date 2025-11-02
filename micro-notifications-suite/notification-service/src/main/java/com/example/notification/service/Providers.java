package com.example.notification.service;

import com.example.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Providers {
  private static final Logger log = LoggerFactory.getLogger(Providers.class);

  private final JavaMailSender mailSender;

  @Autowired
  public Providers(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void send(Notification n){
    switch (n.getChannel()) {
      case "email" -> sendEmail(n);
      case "sms" -> log.info("SMS to {}: {}", n.getRecipientPhone(), n.getContent());
      case "whatsapp" -> log.info("WHATSAPP to {}: {}", n.getRecipientPhone(), n.getContent());
      default -> log.warn("Unknown channel {}", n.getChannel());
    }
  }

  private void sendEmail(Notification n) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(n.getRecipientEmail());
    message.setSubject("Notificación");
    message.setText(n.getContent());
    mailSender.send(message);
    log.info("Email enviado a {}", n.getRecipientEmail());
  }
}
