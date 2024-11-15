package ua.edu.ukma.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.user.enumeration.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    @Email
    private String email;
    @Size(min = 8)
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private Role role;
}
