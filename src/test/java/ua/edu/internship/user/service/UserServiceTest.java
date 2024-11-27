package ua.edu.internship.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.edu.internship.user.utils.TestUtils.getRegistrationDto;
import static ua.edu.internship.user.utils.TestUtils.getUserUpdateDto;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserService underTest;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@gmail.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("email@gmail.com");
    }

    @Test
    void createUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        UserRegistrationDto userRegistrationDto = getRegistrationDto("email@gmail.com", Role.CANDIDATE);
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));

        assertThrows(EmailDuplicateException.class, () -> underTest.createUser(userRegistrationDto));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        UserRegistrationDto userRegistrationDto = getRegistrationDto("email@gmail.com", Role.CANDIDATE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("CANDIDATE");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CANDIDATE")).thenReturn(Optional.of(roleEntity));
        when(mapper.toEntity(userRegistrationDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = underTest.createUser(userRegistrationDto);

        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findByEmail("email@gmail.com");
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        UserUpdateDto userUpdateDto = getUserUpdateDto("email@gmail.com");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));

        assertThrows(EmailDuplicateException.class, () -> underTest.updateUser(1L, userUpdateDto));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    void updateUser_shouldUpdateUser_whenEmailDoesNotExist() {
        UserUpdateDto userUpdateDto = getUserUpdateDto("email@gmail.com");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = underTest.updateUser(1L, userUpdateDto);

        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUserPassword_shouldUpdatePassword() {
        PasswordUpdateDto passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setPassword("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = underTest.updateUserPassword(1L, passwordUpdateDto);

        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = underTest.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = underTest.getUserById(1L);

        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowNoSuchEntityException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = underTest.getUserByEmail("email@gmail.com");

        assertNotNull(result);
        matchUserFields(result, userDto);
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    void getUserByEmail_shouldThrowNoSuchEntityException_whenUserNotFound() {
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> underTest.getUserByEmail("email@gmail.com"));
        verify(userRepository).findByEmail("email@gmail.com");
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        underTest.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    private void matchUserFields(UserDto expected, UserDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getRole(), actual.getRole());
    }
}
