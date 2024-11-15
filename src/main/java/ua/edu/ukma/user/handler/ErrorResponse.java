package ua.edu.ukma.user.handler;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private Map<String, String> errors;
}
