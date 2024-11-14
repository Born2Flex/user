package ua.edu.ukma.user.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.edu.ukma.user.utils.EmailDuplicateException;
import ua.edu.ukma.user.utils.NoSuchEntityException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            fieldErrors.put(fieldName, errorMessage);
                        });
        log.info("Validation failed: {}", fieldErrors, e);
        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchEntityException(NoSuchEntityException e) {
        log.info(e.getMessage(), e);
        ErrorResponse errorResponse =
                new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicateException(EmailDuplicateException e) {
        log.info(e.getMessage(), e);
        Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("email", e.getMessage());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Email Conflict", errorDetail);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
