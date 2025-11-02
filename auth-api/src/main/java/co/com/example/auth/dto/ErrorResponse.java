package co.com.example.auth.dto;

import org.springframework.http.ResponseEntity;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ResponseEntity<ErrorResponse> badRequest(String message) {
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }
}