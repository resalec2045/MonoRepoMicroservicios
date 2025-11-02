package com.example.notification.jobs;

import com.example.notification.domain.Notification;
import com.example.notification.domain.NotificationRepository;
import com.example.notification.service.Providers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Component
public class SchedulerJob {
  private static final Logger log = LoggerFactory.getLogger(SchedulerJob.class);
  private final NotificationRepository repo;
  private final Providers providers;

  public SchedulerJob(NotificationRepository repo, Providers providers) { this.repo = repo; this.providers = providers; }

  @Scheduled(fixedDelay = 5000)
  @Transactional
  public void dispatchScheduled(){
    List<Notification> due = repo.findAll().stream()
        .filter(n -> "SCHEDULED".equals(n.getStatus()) && n.getScheduledAt()!=null && n.getScheduledAt().isBefore(Instant.now()))
        .toList();
    if(!due.isEmpty()) log.info("dispatchScheduled found {} notifications to dispatch", due.size());
    for(Notification n: due){
      try{
        providers.send(n);
        n.setStatus("SENT");
        log.info("dispatched scheduled notification id={} channel={}", n.getId(), n.getChannel());
      }catch(Exception e){
        n.setStatus("FAILED"); n.setErrorMessage(e.getMessage());
        log.error("failed dispatch scheduled notification id={} error={} ", n.getId(), e.getMessage(), e);
      }
      repo.save(n);
    }
  }
}
