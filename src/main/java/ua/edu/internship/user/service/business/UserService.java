package ua.edu.internship.user.service.business;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ua.edu.internship.user.service.message.user.UserDeletedMessage;
import ua.edu.internship.user.service.message.user.UserRegisteredMessage;
import ua.edu.internship.user.service.notification.NotificationSender;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

/**
 * Service responsible for managing user operations.
 * <p>
 * This service handles user creation, updates, retrieval, and deletion.
 *
 * @author Danylo Shlapak
 * @version 1.1
 * @since 1.0
 * @see PasswordEncoder
 * @see UserRepository
 * @see RoleRepository
 * @see NotificationSender
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationSender notificationSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with the provided registration data.
     * <p>
     * Example:
     * <pre>
     * {@code
     * UserRegistrationDto dto = new UserRegistrationDto("email@example.com", "password", Role.USER);
     * UserDto user = userService.createUser(dto);
     * }
     * </pre>
     * @param registrationDto {@link UserRegistrationDto} containing user registration details.
     * @return {@link UserDto} created user.
     * @throws EmailDuplicateException if user with such email already exists.
     */
    public UserDto createUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to create new user");
        checkForDuplicateEmail(registrationDto.getEmail());
        UserEntity userEntity = mapper.mapWithEncodedPassword(registrationDto, passwordEncoder);
        RoleEntity roleEntity = getRoleOrElseThrow(registrationDto.getRole());
        userEntity.setRole(roleEntity);
        UserDto createdUser = mapper.toDto(userRepository.save(userEntity));
        log.info("Created new user with id = {}", createdUser.getId());
        notificationSender.sendNotification(createRegistrationNotification(userEntity));
        return createdUser;
    }

    private BaseUserMessage createRegistrationNotification(UserEntity user) {
        return new UserRegisteredMessage(user.getEmail(), user.getFullName());
    }

    /**
     * Updates an existing user.
     *
     * @param id ID of the user to update.
     * @param updateDto {@link UserUpdateDto} containing updated user data.
     * @return {@link UserDto} updated user.
     * @throws NoSuchEntityException if the user with specified ID does not exist.
     * @throws EmailDuplicateException if user with such email already exists.
     */
    public UserDto updateUser(Long id, UserUpdateDto updateDto) {
        log.info("Attempting to update user with ID: {}", id);
        checkForDuplicateEmail(updateDto.getEmail());
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        UserEntity updatedEntity = mapper.updateEntity(userEntity, updateDto);
        UserDto updatedUser = mapper.toDto(updatedEntity);
        log.info("User with id: {} updated successfully", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Updates the password of an existing user.
     *
     * @param id ID of the user to update password.
     * @param passwordDto {@link PasswordUpdateDto} containing the new password.
     * @return {@link UserDto} user with updated password.
     * @throws NoSuchEntityException if the user with specified ID does not exist.
     */
    public UserDto updateUserPassword(Long id, PasswordUpdateDto passwordDto) {
        log.info("Attempting to update password of user with id: {}", id);
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        UserEntity updatedEntity = mapper.updatePassword(userEntity, passwordDto.getPassword(), passwordEncoder);
        log.info("Password for user with id: {} updated successfully", id);
        return mapper.toDto(updatedEntity);
    }

    /**
     * Retrieves all existing users.
     *
     * @return a list of {@link UserDto}.
     */
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
        log.info("Retrieved {} users from the database", users.size());
        return users;
    }

    /**
     * Retrieves user by ID.
     *
     * @param id ID of the user.
     * @return {@link UserDto} user with specified ID.
     * @throws NoSuchEntityException if the user with specified ID does not exist.
     */
    public UserDto getUserById(Long id) {
        UserDto user = mapper.toDto(getUserByIdOrElseThrow(id));
        log.info("Retrieved user with id: {}", id);
        return user;
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email email of the user.
     * @return {@link UserDto} user with specified email.
     * @throws NoSuchEntityException if the user with specified email does not exist.
     */
    public UserDto getUserByEmail(String email) {
        UserDto user = mapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchEntityException("User not found")));
        log.info("Retrieved user by email: {}", email);
        return user;
    }

    /**
     * Deletes a user by ID and sends a notification.
     *
     * @param id ID of the user.
     * @throws NoSuchEntityException if the user with specified ID does not exist.
     */
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        UserEntity user = getUserByIdOrElseThrow(id);
        userRepository.deleteById(id);
        notificationSender.sendNotification(createDeletionNotification(user));
        log.info("User with id: {} deleted successfully", id);
    }

    private BaseUserMessage createDeletionNotification(UserEntity user) {
        return new UserDeletedMessage(user.getEmail(), user.getFullName());
    }

    /**
     * Checks if a user exists by ID.
     *
     * @param id ID of the user.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
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
