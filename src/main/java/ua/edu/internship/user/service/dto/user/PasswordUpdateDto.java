package ua.edu.internship.user.service.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ua.edu.internship.user.service.utils.validation.annotations.password.Password;

@Data
public class PasswordUpdateDto {
    @Email
    private String email;
    @Password
    private String password;
}
