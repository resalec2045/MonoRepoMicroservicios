package com.example.orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  @Value("${app.rabbit.exchange}") String exchangeName;

  @Bean public TopicExchange exchange(){ return new TopicExchange(exchangeName, true, false); }
  @Bean public Queue qReg(){ return QueueBuilder.durable("auth.user.registered").build(); }
  @Bean public Queue qLogin(){ return QueueBuilder.durable("auth.user.loggedin").build(); }
  @Bean public Queue qReset(){ return QueueBuilder.durable("auth.user.password.reset.requested").build(); }
  @Bean public Queue qUpdated(){ return QueueBuilder.durable("auth.user.password.updated").build(); }

  @Bean public Binding bReg(TopicExchange e, @Qualifier("qReg") Queue qReg){ return BindingBuilder.bind(qReg).to(e).with("user.registered"); }
  @Bean public Binding bLogin(TopicExchange e, @Qualifier("qLogin") Queue qLogin){ return BindingBuilder.bind(qLogin).to(e).with("user.loggedin"); }
  @Bean public Binding bReset(TopicExchange e, @Qualifier("qReset") Queue qReset){ return BindingBuilder.bind(qReset).to(e).with("user.password.reset.requested"); }
  @Bean public Binding bUpdated(TopicExchange e, @Qualifier("qUpdated") Queue qUpdated){ return BindingBuilder.bind(qUpdated).to(e).with("user.password.updated"); }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
