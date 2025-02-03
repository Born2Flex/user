package ua.edu.internship.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ua.edu.internship.user.data.entity.UserEntity;
import ua.edu.internship.user.data.repository.UserRepository;
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.dto.user.UserUpdateDto;
import ua.edu.internship.user.service.enumeration.Role;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ua.edu.internship.user.utils.TestUtils.getRegistrationDto;
import static ua.edu.internship.user.utils.TestUtils.getUpdateDto;
import static ua.edu.internship.user.utils.TestUtils.getUserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Transactional
class UserServiceIntegrationTest {
    private final UserService underTest;
    private final UserRepository userRepository;
    private UserDto savedUser;
    private UserDto savedInterviewer;

    @Autowired
    public UserServiceIntegrationTest(UserService underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        savedUser = getUserDto(1L, "John", "Doe", "user@gmail.com", Role.CANDIDATE);
        savedInterviewer = getUserDto(2L, "Jane", "Doe", "interviewer@gmail.com", Role.INTERVIEWER);
    }

    @Test
    @DisplayName("Should create new user when email is not already in use")
    void shouldCreateUserWhenEmailDoesNotExist() {
        // given
        UserRegistrationDto userRegistrationDto =
                getRegistrationDto("John", "Doe", "newemail@gmail.com", "password", Role.CANDIDATE);

        // when
        UserDto result = underTest.createUser(userRegistrationDto);

        // then
        assertNotNull(result);
        matchUserFields(userRegistrationDto, result);
        matchUserFields(result, fetchUserById(result.getId()));
    }

    @Test
    @DisplayName("Should update user details successfully")
    void shouldUpdateUserDetails() {
        // given
        UserUpdateDto updateDto = getUpdateDto("Rob", "Martin", "updated@gmail.com", Role.INTERVIEWER);

        // when
        UserDto updatedUser = underTest.updateUser(1L, updateDto);

        // then
        assertNotNull(updatedUser);
        matchUserFields(updateDto, updatedUser);
        matchUserFields(updatedUser, fetchUserById(updatedUser.getId()));
    }

    @Test
    @DisplayName("Should throw EmailDuplicateException when trying to create user with email that is already exists")
    void shouldThrowEmailDuplicateExceptionWhenEmailExists() {
        // given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("user@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setRole(Role.CANDIDATE);

        // when
        // then
        assertThrows(EmailDuplicateException.class, () -> underTest.createUser(userRegistrationDto));
    }

    @Test
    @DisplayName("Should return list of all users stored in DB")
    void getAllUsersShouldReturnListOfUsers() {
        // given
        // when
        List<UserDto> result = underTest.getAllUsers();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, userRepository.count());
        assertTrue(result.containsAll(List.of(savedUser, savedInterviewer)));
    }

    @Test
    @DisplayName("Should return user by ID")
    void getUserByIdShouldReturnUser() {
        // given
        // when
        UserDto result = underTest.getUserById(1L);

        // then
        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when user does not exists by ID")
    void shouldThrowExceptionWhenUserNotFoundByID() {
        // given
        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserById(999L));
    }

    @Test
    @DisplayName("Should return user by email")
    void shouldReturnUserByEmail() {
        // given
        // when
        UserDto result = underTest.getUserByEmail("user@gmail.com");

        // then
        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    @DisplayName("Should throw NoSuchEntityException when user does not exists by email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // given
        // when
        // then
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserByEmail("nonexistentemail@gmail.com"));
    }

    private UserEntity fetchUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    private void matchUserFields(UserRegistrationDto expected, UserDto actual) {
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getRole(), actual.getRole());
    }

    private void matchUserFields(UserDto expected, UserDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getRole(), actual.getRole());
    }

    private void matchUserFields(UserDto expected, UserEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getRole().name(), actual.getRole().getName());
    }

    private void matchUserFields(UserUpdateDto expected, UserDto actual) {
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }
}
