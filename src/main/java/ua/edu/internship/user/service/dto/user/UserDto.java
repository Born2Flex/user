package ua.edu.internship.user.service.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends UserBaseDto {
    private Long id;
}
