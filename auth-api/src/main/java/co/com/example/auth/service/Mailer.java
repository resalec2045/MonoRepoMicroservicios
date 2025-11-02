package co.com.example.auth.service;

public interface Mailer {
    void send(String to, String subject, String body);
}
