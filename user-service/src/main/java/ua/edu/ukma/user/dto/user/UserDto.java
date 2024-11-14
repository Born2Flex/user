package ua.edu.ukma.user.dto.user;

import lombok.Data;
import ua.edu.ukma.user.dto.role.RoleDto;

@Data
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleDto role;
}
