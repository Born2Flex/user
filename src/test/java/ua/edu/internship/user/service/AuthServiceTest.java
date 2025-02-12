package ua.edu.internship.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.internship.user.config.security.TokenData;
import ua.edu.internship.user.data.entity.PermissionEntity;
import ua.edu.internship.user.data.entity.RoleEntity;
import ua.edu.internship.user.data.entity.UserEntity;
import ua.edu.internship.user.data.repository.UserRepository;
import ua.edu.internship.user.service.business.AuthService;
import ua.edu.internship.user.service.business.JwtService;
import ua.edu.internship.user.service.dto.auth.LoginDto;
import ua.edu.internship.user.service.dto.auth.TokenDto;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    private UserEntity userEntity;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@gmail.com");
        userEntity.setPassword("encodedPassword");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("USER");
        roleEntity.setPermissions(Set.of(PermissionEntity.builder().id(1L).name("READ").build()));
        userEntity.setRole(roleEntity);

        loginDto = new LoginDto();
        loginDto.setEmail("email@gmail.com");
        loginDto.setPassword("rawPassword");
    }

    @Test
    @DisplayName("Should authenticate user and return token")
    void shouldAuthenticateUserAndReturnToken() {
        // given
        TokenData tokenData = new TokenData(1L, Set.of("READ"));
        String generatedToken = "mockToken";
        when(userRepo.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(tokenData)).thenReturn(generatedToken);

        // when
        TokenDto tokenDto = authService.authenticate(loginDto);

        // then
        assertNotNull(tokenDto);
        assertEquals(generatedToken, tokenDto.getToken());
        verify(userRepo).findByEmail("email@gmail.com");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
        verify(jwtService).generateToken(tokenData);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for incorrect password")
    void shouldThrowBadCredentialsExceptionForIncorrectPassword() {
        // given
        when(userRepo.findByEmail("email@gmail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(false);

        // when
        // then
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginDto));
        verify(userRepo).findByEmail("email@gmail.com");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepo.findByEmail("email@gmail.com")).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginDto));
        verify(userRepo).findByEmail("email@gmail.com");
    }
}
