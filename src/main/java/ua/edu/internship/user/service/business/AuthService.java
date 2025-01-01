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

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public TokenDto authenticate(LoginDto dto) {
        UserEntity user = userRepo.findByEmail(dto.getEmail()).orElseThrow();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
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
