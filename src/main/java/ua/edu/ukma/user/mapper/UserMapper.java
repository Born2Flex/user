package ua.edu.ukma.user.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.ukma.user.dto.user.PasswordUpdateDto;
import ua.edu.ukma.user.dto.user.UserDto;
import ua.edu.ukma.user.dto.user.UserRegistrationDto;
import ua.edu.ukma.user.dto.user.UserUpdateDto;
import ua.edu.ukma.user.entity.UserEntity;

@Mapper(componentModel = SPRING, uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(source = "role", target = "role", ignore = true)
    UserEntity toEntity(UserRegistrationDto dto);

    UserDto toDto(UserEntity entity);

    void updateEntity(UserUpdateDto dto, @MappingTarget UserEntity entity);

    void updatePassword(PasswordUpdateDto dto, @MappingTarget UserEntity entity);
}
