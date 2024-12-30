package ua.edu.internship.user.config.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response")
public class ApiErrorDto {
    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;
    @Schema(description = "HTTP status code", example = "BAD_REQUEST")
    private int statusCode;
    @Schema(description = "Error message", example = "Validation failed")
    private String message;
    @Schema(description = "Error code", example = "1")
    private Integer errorCode;
}