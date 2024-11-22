package ua.edu.internship.user.service.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import org.mapstruct.Mapper;
import ua.edu.internship.user.service.dto.role.RoleDto;
import ua.edu.internship.user.data.entity.RoleEntity;

@Mapper(componentModel = SPRING)
public interface RoleMapper {
    RoleDto toDto(RoleEntity entity);
}
