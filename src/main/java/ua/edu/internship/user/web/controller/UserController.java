package ua.edu.internship.user.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;
import ua.edu.internship.user.service.dto.user.PasswordUpdateDto;
import ua.edu.internship.user.service.dto.user.UserDto;
import ua.edu.internship.user.service.dto.user.UserRegistrationDto;
import ua.edu.internship.user.service.dto.user.UserUpdateDto;
import ua.edu.internship.user.service.business.UserService;
import ua.edu.internship.user.web.handler.ErrorResponse;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserRegistrationDto dto) {
        return service.createUser(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user data")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDto dto) {
        return service.updateUser(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto updateUserPassword(@PathVariable Long id, @RequestBody @Valid PasswordUpdateDto dto) {
        return service.updateUserPassword(id, dto);
    }

    @GetMapping
    @Operation(summary = "Get information about all users")
    @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(
            schema = @Schema(implementation = UserDto.class)), mediaType = "application/json")})
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about user by id")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get information about user by email")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Check if a user exists by id")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Boolean.class),
            mediaType = "application/json")})
    public boolean userExists(@PathVariable Long id) {
        return service.userExists(id);
    }
}
