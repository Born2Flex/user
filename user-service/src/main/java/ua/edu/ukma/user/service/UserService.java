package ua.edu.ukma.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.user.dto.user.PasswordUpdateDto;
import ua.edu.ukma.user.dto.user.UserDto;
import ua.edu.ukma.user.dto.user.UserRegistrationDto;
import ua.edu.ukma.user.dto.user.UserUpdateDto;
import ua.edu.ukma.user.entity.RoleEntity;
import ua.edu.ukma.user.entity.UserEntity;
import ua.edu.ukma.user.enumeration.Role;
import ua.edu.ukma.user.mapper.UserMapper;
import ua.edu.ukma.user.repository.RoleRepository;
import ua.edu.ukma.user.repository.UserRepository;
import ua.edu.ukma.user.utils.EmailDuplicateException;
import ua.edu.ukma.user.utils.NoSuchEntityException;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserMapper mapper;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserDto createUser(UserRegistrationDto dto) {
        validateEmail(dto.getEmail());
        UserEntity userEntity = mapper.toEntity(dto);
        RoleEntity roleEntity = getRoleOrElseThrow(dto.getRole());
        userEntity.setRole(roleEntity);
        return mapper.toDto(userRepository.save(userEntity));
    }

    public UserDto updateUser(Integer id, UserUpdateDto dto) {
        validateEmail(dto.getEmail());
        UserEntity userEntity = getUserOrElseThrow(id);
        mapper.updateEntity(dto, userEntity);
        return mapper.toDto(userEntity);
    }

    public UserDto updateUserPassword(Integer id, PasswordUpdateDto dto) {
        UserEntity userEntity = getUserOrElseThrow(id);
        mapper.updatePassword(dto, userEntity);
        return mapper.toDto(userEntity);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserDto getUserById(Integer id) {
        return mapper.toDto(getUserOrElseThrow(id));
    }

    public UserDto getUserByEmail(String email) {
        return mapper.toDto(
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new NoSuchEntityException("User not found")));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    private UserEntity getUserOrElseThrow(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));
    }

    private RoleEntity getRoleOrElseThrow(Role role) {
        return roleRepository
                .findByName(role.name())
                .orElseThrow(() -> new NoSuchEntityException("Role not found"));
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailDuplicateException();
        }
    }
}
