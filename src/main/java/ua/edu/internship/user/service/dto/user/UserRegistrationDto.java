package ua.edu.internship.user.service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.internship.user.service.utils.validation.annotations.password.Password;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto extends UserBaseDto {
    @Password
    private String password;
}
