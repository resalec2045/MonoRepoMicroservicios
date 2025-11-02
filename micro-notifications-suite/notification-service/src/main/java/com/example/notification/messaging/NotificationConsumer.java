package com.example.notification.messaging;

import com.example.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class NotificationConsumer {
  private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
  private final NotificationService service;

  public NotificationConsumer(NotificationService service) { this.service = service; }

  @RabbitListener(queues = "notify.request")
  public void onNotify(@Payload Map<String, Object> payload){
    String channel = (String) payload.getOrDefault("channel","email");
    String email = (String) payload.get("email");
    String phone = (String) payload.get("phone");
    String content = (String) payload.getOrDefault("content","");
    log.info("notify.request received channel={} email={} phone={}", channel, email, phone);
    service.createAndSend(channel, email, phone, content);
  }
}
