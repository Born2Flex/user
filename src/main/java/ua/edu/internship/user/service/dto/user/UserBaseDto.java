package ua.edu.internship.user.service.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.internship.user.service.enumeration.Role;
import ua.edu.internship.user.service.utils.validation.annotations.name.Name;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserBaseDto {
    @Email
    private String email;
    @Name
    private String firstName;
    @Name
    private String lastName;
    private Role role;
}
