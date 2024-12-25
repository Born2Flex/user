package ua.edu.internship.user.service.business;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ua.edu.internship.user.service.message.user.BaseUserMessage;
import ua.edu.internship.user.service.message.user.UserRegisteredMessage;
import ua.edu.internship.user.service.notification.NotificationSender;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationSender notificationSender;

    public UserDto createUser(UserRegistrationDto dto) {
        log.info("Attempting to create new user");
        checkForDuplicateEmail(dto.getEmail());
        UserEntity userEntity = mapper.toEntity(dto);
        RoleEntity roleEntity = getRoleOrElseThrow(dto.getRole());
        userEntity.setRole(roleEntity);
        UserDto createdUser = mapper.toDto(userRepository.save(userEntity));
        log.info("Created new user with id = {}", createdUser.getId());
        notificationSender.sendNotification(createRegistrationNotification(userEntity));
        return createdUser;
    }

    private BaseUserMessage createRegistrationNotification(UserEntity user) {
        return new UserRegisteredMessage(user.getEmail(), user.getFullName());
    }

    public UserDto updateUser(Long id, UserUpdateDto dto) {
        log.info("Attempting to update user with ID: {}", id);
        checkForDuplicateEmail(dto.getEmail());
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        mapper.updateEntity(dto, userEntity);
        UserDto updatedUser = mapper.toDto(userEntity);
        log.info("User with id: {} updated successfully", updatedUser.getId());
        return updatedUser;
    }

    public UserDto updateUserPassword(Long id, PasswordUpdateDto dto) {
        log.info("Attempting to update password of user with id: {}", id);
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        mapper.updatePassword(dto.getPassword(), userEntity);
        log.info("Password for user with id: {} updated successfully", id);
        return mapper.toDto(userEntity);
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
        log.info("Retrieved {} users from the database", users.size());
        return users;
    }

    public UserDto getUserById(Long id) {
        UserDto user = mapper.toDto(getUserByIdOrElseThrow(id));
        log.info("Retrieved user with id: {}", id);
        return user;
    }

    public UserDto getUserByEmail(String email) {
        UserDto user = mapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchEntityException("User not found")));
        log.info("Retrieved user by email: {}", email);
        return user;
    }

    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        UserEntity user = getUserByIdOrElseThrow(id);
        userRepository.deleteById(id);
        notificationSender.sendNotification(createDeletionNotification(user));
        log.info("User with id: {} deleted successfully", id);
    }

    private BaseUserMessage createDeletionNotification(UserEntity user) {
        return new UserRegisteredMessage(user.getEmail(), user.getFullName());
    }

    public boolean userExists(Long id) {
        log.info("Validating that user with id: {} exists", id);
        return userRepository.existsById(id);
    }

    private UserEntity getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));
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
