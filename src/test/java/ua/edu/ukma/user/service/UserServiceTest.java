package ua.edu.ukma.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserService userService;
    private UserRegistrationDto userRegistrationDto;
    private UserUpdateDto userUpdateDto;
    private PasswordUpdateDto passwordUpdateDto;
    private UserEntity userEntity;
    private UserDto userDto;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("email@gmail.com");
        userRegistrationDto.setRole(Role.CANDIDATE);

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("email@gmail.com");

        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setPassword("password");

        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail("email@gmail.com");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("email@gmail.com");

        roleEntity = new RoleEntity();
        roleEntity.setName("CANDIDATE");
    }

    @Test
    void createUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        assertThrows(EmailDuplicateException.class, () -> userService.createUser(userRegistrationDto));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(roleEntity));
        when(mapper.toEntity(any(UserRegistrationDto.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        UserDto result = userService.createUser(userRegistrationDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        assertThrows(EmailDuplicateException.class, () -> userService.updateUser(1, userUpdateDto));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void updateUser_shouldUpdateUser_whenEmailDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        UserDto result = userService.updateUser(1, userUpdateDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void updateUserPassword_shouldUpdatePassword() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        UserDto result = userService.updateUserPassword(1, passwordUpdateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        UserDto result = userService.getUserById(1);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void getUserById_shouldThrowNoSuchEntityException_whenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> userService.getUserById(1));
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

        UserDto result = userService.getUserByEmail("email@gmail.com");

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void getUserByEmail_shouldThrowNoSuchEntityException_whenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> userService.getUserByEmail("email@gmail.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(anyInt());

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(anyInt());
    }
}
