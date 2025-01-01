package ua.edu.internship.user.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.user.service.business.AuthService;
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.service.dto.auth.LoginDto;
import ua.edu.internship.user.service.dto.auth.TokenDto;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@RequestBody @Valid UserRegistrationDto dto) {
        return userService.createUser(dto);
    }

    @PostMapping("/login")
    public TokenDto authorize(@RequestBody LoginDto dto) {
        return authService.authenticate(dto);
    }
}
