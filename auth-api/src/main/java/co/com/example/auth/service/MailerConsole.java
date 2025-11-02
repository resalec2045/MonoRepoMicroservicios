package co.com.example.auth.service;

import org.springframework.stereotype.Service;

@Service
public class MailerConsole implements Mailer {
    @Override
    public void send(String to, String subject, String body) {
        System.out.println("[MAILER] Asunto: " + subject);
        System.out.println("[MAILER] Cuerpo: " + body);
    }
}
