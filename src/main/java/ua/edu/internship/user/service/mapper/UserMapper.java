package ua.edu.internship.user.service.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    UserEntity updateEntity(@MappingTarget UserEntity entity, UserUpdateDto dto);

    default UserEntity updatePassword(UserEntity entity, String password, PasswordEncoder passwordEncoder) {
        entity.setPassword(passwordEncoder.encode(password));
        return entity;
    }

    default UserEntity mapWithEncodedPassword(UserRegistrationDto dto, PasswordEncoder passwordEncoder) {
        UserEntity userEntity = toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userEntity;
    }
}
