package ua.edu.internship.user.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ua.edu.internship.user.service.business.AuthService;
import ua.edu.internship.user.service.dto.auth.LoginDto;
import ua.edu.internship.user.service.dto.auth.TokenDto;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.web.handler.ErrorResponse;

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User successfully registered",
            content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody @Valid UserRegistrationDto dto) {
        return userService.createUser(dto);
    }

    @PostMapping("/login")
    public TokenDto authorize(@RequestBody LoginDto dto) {
        return authService.authenticate(dto);
    }
}
