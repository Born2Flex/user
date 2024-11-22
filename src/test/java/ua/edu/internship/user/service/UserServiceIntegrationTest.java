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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UserServiceIntegrationTest {
    private final UserService userService;
    private UserDto savedUser;
    private UserDto savedInterviewer;

    @Autowired
    public UserServiceIntegrationTest(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        savedUser = new UserDto();
        savedUser.setId(1L);
        savedUser.setEmail("user@gmail.com");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setRole(Role.CANDIDATE);

        savedInterviewer = new UserDto();
        savedInterviewer.setId(2L);
        savedInterviewer.setEmail("interviewer@gmail.com");
        savedInterviewer.setFirstName("Jane");
        savedInterviewer.setLastName("Doe");
        savedInterviewer.setRole(Role.INTERVIEWER);
    }

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("John");
        userRegistrationDto.setLastName("Doe");
        userRegistrationDto.setEmail("newemail@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setRole(Role.CANDIDATE);

        UserDto result = userService.createUser(userRegistrationDto);

        assertNotNull(result);
        matchUserFields(userRegistrationDto, result);
    }

    @Test
    void createUser_shouldThrowEmailDuplicateException_whenEmailExists() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("user@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setRole(Role.CANDIDATE);

        assertThrows(EmailDuplicateException.class, () -> userService.createUser(userRegistrationDto));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(savedUser, savedInterviewer)));
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    void getUserById_shouldThrowNoSuchEntityException_whenUserNotFound() {
        assertThrows(NoSuchEntityException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        UserDto result = userService.getUserByEmail("user@gmail.com");

        assertNotNull(result);
        matchUserFields(savedUser, result);
    }

    @Test
    void getUserByEmail_shouldThrowNoSuchEntityException_whenUserNotFound() {
        assertThrows(NoSuchEntityException.class, () -> userService.getUserByEmail("nonexistentemail@gmail.com"));
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
