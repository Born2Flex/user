package ua.edu.ukma.user.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.edu.ukma.user.dto.user.UserDto;
import ua.edu.ukma.user.dto.user.UserRegistrationDto;
import ua.edu.ukma.user.enumeration.Role;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceIntegrationTest {
  private final UserService userService;

  @Autowired
  public UserServiceIntegrationTest(UserService userService) {
    this.userService = userService;
  }

  @Test
  void test() {
    UserRegistrationDto user = new UserRegistrationDto("john@gmail.com", "password", "John", "Doe", Role.CANDIDATE);
    UserDto userDto = userService.createUser(user);
    assertNotNull(userDto);
  }
}
