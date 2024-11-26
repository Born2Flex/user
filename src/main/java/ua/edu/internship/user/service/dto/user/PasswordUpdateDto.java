package ua.edu.internship.user.service.dto.user;

import lombok.Data;
import ua.edu.internship.user.service.utils.validation.annotations.password.Password;

@Data
public class PasswordUpdateDto {
    @Password
    private String password;
}
