package ua.edu.ukma.user.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.user.dto.user.PasswordUpdateDto;
import ua.edu.ukma.user.dto.user.UserDto;
import ua.edu.ukma.user.dto.user.UserRegistrationDto;
import ua.edu.ukma.user.dto.user.UserUpdateDto;
import ua.edu.ukma.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserRegistrationDto dto) {
        return service.createUser(dto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Integer id, @RequestBody @Valid UserUpdateDto dto) {
        return service.updateUser(id, dto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserPassword(
            @PathVariable Integer id, @RequestBody @Valid PasswordUpdateDto dto) {
        return service.updateUserPassword(id, dto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    @PostMapping("/by-email")
    public UserDto getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
    }
}
