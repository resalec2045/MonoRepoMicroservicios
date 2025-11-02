package com.example.auth.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventPublisher {
  private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;

  public EventPublisher(RabbitTemplate rabbitTemplate, @Value("${app.rabbit.exchange}") String exchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
  }

  public void publish(String type, String email, String phone, String payload){
    log.debug("publishing event type={} email={}", type, email);
    rabbitTemplate.convertAndSend(exchange, type, new AuthEvent(type, email, phone, payload));
  }
}
