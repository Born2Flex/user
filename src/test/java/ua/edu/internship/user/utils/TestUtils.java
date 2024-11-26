package ua.edu.internship.user.utils;

import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.dto.user.UserUpdateDto;
import ua.edu.internship.user.service.enumeration.Role;

public final class TestUtils {

    public static UserRegistrationDto getRegistrationDto(String email, Role role) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail(email);
        dto.setRole(role);
        return dto;
    }

    public static UserRegistrationDto getRegistrationDto(String firstName, String lastName, String email, String password, Role role) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        return dto;
    }

    public static UserDto getUserDto(Long id, String firstName, String lastName, String email, Role role) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setRole(role);
        return dto;
    }

    public static UserUpdateDto getUserUpdateDto(String email) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail(email);
        return userUpdateDto;
    }


}
