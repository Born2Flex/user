package ua.edu.internship.user.service.business;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.internship.user.service.dto.user.PasswordUpdateDto;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.dto.user.UserUpdateDto;
import ua.edu.internship.user.data.entity.RoleEntity;
import ua.edu.internship.user.data.entity.UserEntity;
import ua.edu.internship.user.service.enumeration.Role;
import ua.edu.internship.user.service.mapper.UserMapper;
import ua.edu.internship.user.data.repository.RoleRepository;
import ua.edu.internship.user.data.repository.UserRepository;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDto createUser(UserRegistrationDto dto) {
        checkForDuplicateEmail(dto.getEmail());
        UserEntity userEntity = mapper.toEntity(dto);
        RoleEntity roleEntity = getRoleOrElseThrow(dto.getRole());
        userEntity.setRole(roleEntity);
        return mapper.toDto(userRepository.save(userEntity));
    }

    public UserDto updateUser(Long id, UserUpdateDto dto) {
        checkForDuplicateEmail(dto.getEmail());
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        mapper.updateEntity(dto, userEntity);
        return mapper.toDto(userEntity);
    }

    public UserDto updateUserPassword(PasswordUpdateDto dto) {
        UserEntity userEntity = getUserByEmailOrElseThrow(dto.getEmail());
        mapper.updatePassword(dto.getPassword(), userEntity);
        return mapper.toDto(userEntity);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        return mapper.toDto(getUserByIdOrElseThrow(id));
    }

    public UserDto getUserByEmail(String email) {
        return mapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchEntityException("User not found")));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserEntity getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));
    }

    private UserEntity getUserByEmailOrElseThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchEntityException("User not found by email"));
    }

    private RoleEntity getRoleOrElseThrow(Role role) {
        return roleRepository.findByName(role.name())
                .orElseThrow(() -> new NoSuchEntityException("Role not found"));
    }

    private void checkForDuplicateEmail(String email) {
        userRepository.findByEmail(email).ifPresent(this::throwEmailDuplicateException);
    }

    private void throwEmailDuplicateException(UserEntity user) {
        throw new EmailDuplicateException();
    }
}
