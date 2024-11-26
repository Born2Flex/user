package ua.edu.internship.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.enumeration.Role;
import ua.edu.internship.user.service.utils.exceptions.EmailDuplicateException;
import ua.edu.internship.user.service.utils.exceptions.NoSuchEntityException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ua.edu.internship.user.utils.TestUtils.getRegistrationDto;
import static ua.edu.internship.user.utils.TestUtils.getUserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UserServiceIntegrationTest {
    private final UserService underTest;
    private UserDto savedUser;
    private UserDto savedInterviewer;

    @Autowired
    public UserServiceIntegrationTest(UserService underTest) {
        this.underTest = underTest;
    }

    @BeforeEach
    void setUp() {
        savedUser = getUserDto(1L, "John", "Doe", "user@gmail.com", Role.CANDIDATE);
        savedInterviewer = getUserDto(2L, "Jane", "Doe", "interviewer@gmail.com", Role.INTERVIEWER);
    }

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        UserRegistrationDto userRegistrationDto =
                getRegistrationDto("John", "Doe", "newemail@gmail.com", "password", Role.CANDIDATE);

        UserDto result = underTest.createUser(userRegistrationDto);

        assertNotNull(result);
        matchUserFields(userRegistrationDto, result);
    }

    @Test
    void createUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("user@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setRole(Role.CANDIDATE);

        assertThrows(EmailDuplicateException.class, () -> underTest.createUser(userRegistrationDto));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<UserDto> result = underTest.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(savedUser, savedInterviewer)));
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto result = underTest.getUserById(1L);

        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    void getUserById_shouldThrowNoSuchEntityException_whenUserNotFound() {
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserById(999L));
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        UserDto result = underTest.getUserByEmail("user@gmail.com");

        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    void getUserByEmail_shouldThrowNoSuchEntityException_whenUserNotFound() {
        assertThrows(NoSuchEntityException.class, () -> underTest.getUserByEmail("nonexistentemail@gmail.com"));
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
}
