package ua.edu.internship.user.service.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.dto.user.UserUpdateDto;
import ua.edu.internship.user.data.entity.UserEntity;

@Mapper(componentModel = SPRING, uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(source = "role", target = "role", ignore = true)
    UserEntity toEntity(UserRegistrationDto dto);

    @Mapping(source = "role.name", target = "role")
    UserDto toDto(UserEntity entity);

    void updateEntity(UserUpdateDto dto, @MappingTarget UserEntity entity);

    void updatePassword(String password, @MappingTarget UserEntity entity);
}
