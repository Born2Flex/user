package ua.edu.ukma.user.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDto {
    @Size(min = 8)
    private String password;
}
