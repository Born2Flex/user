package ua.edu.internship.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.edu.internship.user.utils.TestUtils.getRegistrationDto;
import static ua.edu.internship.user.utils.TestUtils.getUserUpdateDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.service.message.user.UserDeletedMessage;
import ua.edu.internship.user.service.message.user.UserRegisteredMessage;
import ua.edu.internship.user.service.notification.NotificationSender;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private NotificationSender notificationSender;
    @InjectMocks
    private UserService underTest;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@gmail.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should throw EmailDuplicateException when trying to create user with email that is already exists")
    void shouldThrowEmailDuplicateExceptionWhenCreatingUserWithEmailThatAlreadyExists() {
        // given
        UserRegistrationDto userRegistrationDto = getRegistrationDto("email@gmail.com", Role.CANDIDATE);
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));

        // when
        // then
        assertThrows(EmailDuplicateException.class, () -> underTest.createUser(userRegistrationDto));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should create new user when email is not already in use")
    void shouldCreateNewUser() {
        // given
        UserRegistrationDto userRegistrationDto = getRegistrationDto("email@gmail.com", Role.CANDIDATE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("CANDIDATE");
        UserRegisteredMessage notification = new UserRegisteredMessage("email@gmail.com", "John Doe");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CANDIDATE")).thenReturn(Optional.of(roleEntity));
        when(mapper.mapWithEncodedPassword(userRegistrationDto, passwordEncoder)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);
        doNothing().when(notificationSender).sendNotification(notification);

        // when
        UserDto result = underTest.createUser(userRegistrationDto);

        // then
        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findByEmail("email@gmail.com");
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Should send notification when user successfully registered")
    void shouldSendNotificationWhenUserSuccessfullyRegistered() {
        // given
        UserRegistrationDto userRegistrationDto = getRegistrationDto("email@gmail.com", Role.CANDIDATE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("CANDIDATE");
        UserRegisteredMessage notification = new UserRegisteredMessage("email@gmail.com", "John Doe");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CANDIDATE")).thenReturn(Optional.of(roleEntity));
        when(mapper.mapWithEncodedPassword(userRegistrationDto, passwordEncoder)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);
        doNothing().when(notificationSender).sendNotification(notification);

        // when
        UserDto result = underTest.createUser(userRegistrationDto);

        // then
        assertNotNull(result);
        verify(notificationSender).sendNotification(notification);
    }

    @Test
    @DisplayName("Should throw EmailDuplicateException when trying to user with email that is already exists")
    void shouldThrowEmailDuplicateExceptionWhenUpdatingUserWithEmailThatAlreadyExists () {
        // given
        UserUpdateDto userUpdateDto = getUserUpdateDto("email@gmail.com");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));

        // when
        // then
        assertThrows(EmailDuplicateException.class, () -> underTest.updateUser(1L, userUpdateDto));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should update user when email is not already in use")
    void shouldUpdateUser() {
        // given
        UserUpdateDto userUpdateDto = getUserUpdateDto("email@gmail.com");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.updateEntity(userEntity, userUpdateDto)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto result = underTest.updateUser(1L, userUpdateDto);

        // then
        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
        verify(mapper).updateEntity(userEntity, userUpdateDto);
        verify(mapper).toDto(userEntity);
    }

    @Test
    @DisplayName("Should update user password when it's valid")
    void shouldUpdateUserPassword() {
        // given
        PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setPassword("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.updatePassword(userEntity, "password", passwordEncoder)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto result = underTest.updateUserPassword(1L, passwordUpdateDto);

        // then
        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
        verify(mapper).updatePassword(userEntity, "password", passwordEncoder);
        verify(mapper).toDto(userEntity);
    }

    @Test
    @DisplayName("Should return list of all users stored in DB")
    void shouldReturnAllUsersStoredInDB() {
        // given
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        // when
        List<UserDto> result = underTest.getAllUsers();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    void shouldReturnUserByID() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto result = underTest.getUserById(1L);

        // then
        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when user does not exists by ID")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return user by email")
    void shouldReturnUserByEmail() {
        // given
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        // when
        UserDto result = underTest.getUserByEmail("email@gmail.com");

        // then
        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when user does not exists by email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // given
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserByEmail("email@gmail.com"));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserByID() {
        // given
        UserDeletedMessage notification = new UserDeletedMessage("email@gmail.com", "John Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).deleteById(1L);
        doNothing().when(notificationSender).sendNotification(notification);

        // when
        underTest.deleteUser(1L);

        // then
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should send notification when user deleted")
    void shouldSendNotificationWhenUserDeleted() {
        // given
        UserDeletedMessage notification = new UserDeletedMessage("email@gmail.com", "John Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).deleteById(1L);
        doNothing().when(notificationSender).sendNotification(notification);

        // when
        underTest.deleteUser(1L);

        // then
        verify(userRepository).deleteById(1L);
        verify(notificationSender).sendNotification(notification);
    }

    @Test
    @DisplayName("Should return True when user exist by ID")
    void shouldReturnTrueWhenUserExistsByID() {
        // given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // when
        boolean result = underTest.userExists(userId);

        // then
        assertTrue(result);
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("Should return False when user doesn't exist by ID")
    void shouldReturnFalseWhenUserDoesNotExists() {
        // given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // when
        boolean result = underTest.userExists(userId);

        // then
        assertFalse(result);
        verify(userRepository).existsById(userId);
    }

    private void matchUserFields(UserDto expected, UserDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getRole(), actual.getRole());
    }
}
