package ua.edu.internship.user.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.edu.internship.user.config.security.TokenData;
import ua.edu.internship.user.data.entity.PermissionEntity;
import ua.edu.internship.user.data.entity.UserEntity;
import ua.edu.internship.user.data.repository.UserRepository;
import ua.edu.internship.user.service.dto.auth.LoginDto;
import ua.edu.internship.user.service.dto.auth.TokenDto;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for user authentication.
 *
 * @see PasswordEncoder
 * @see JwtService
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on their login credentials and generates a JWT token.
     * <p>
     * This method validates the provided email and password, retrieves the user's role and permissions,
     * and issues a secure JWT token to facilitate authenticated access to the system.
     *
     * @param loginDto {@link LoginDto} containing the user's email and password.
     * @return {@link TokenDto} with the generated JWT token.
     * @throws BadCredentialsException if the provided credentials are invalid.
     */
    public TokenDto authenticate(LoginDto loginDto) {
        UserEntity user = userRepo.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        Set<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());
        String token = jwtService.generateToken(new TokenData(user.getId(), permissions));
        return new TokenDto(token);
    }
}
