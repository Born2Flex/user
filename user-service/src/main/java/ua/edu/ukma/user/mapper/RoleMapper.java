package ua.edu.ukma.user.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import org.mapstruct.Mapper;
import ua.edu.ukma.user.dto.role.RoleDto;
import ua.edu.ukma.user.entity.RoleEntity;

@Mapper(componentModel = SPRING)
public interface RoleMapper {
    RoleDto toDto(RoleEntity entity);
}
