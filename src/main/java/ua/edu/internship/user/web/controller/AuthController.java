package ua.edu.internship.user.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.user.service.business.AuthService;
import ua.edu.internship.user.service.dto.auth.LoginDto;
import ua.edu.internship.user.service.dto.auth.TokenDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public TokenDto authorize(@RequestBody LoginDto dto) {
        return authService.authenticate(dto);
    }
}
