package ua.edu.internship.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ua.edu.internship.user.config.security.TokenData;
import ua.edu.internship.user.service.business.JwtService;
import ua.edu.internship.user.service.utils.exceptions.InvalidTokenException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static ua.edu.internship.user.service.business.JwtService.TOKEN_DATA_TYPE_REF;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", "99A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75328955");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 10L);
    }

    @Test
    @DisplayName("Should generate a valid JWT token")
    void shouldGenerateValidJwtToken() {
        // given
        TokenData tokenData = new TokenData(1L, Set.of("READ", "WRITE"));
        when(mapper.convertValue(tokenData, TOKEN_DATA_TYPE_REF))
                .thenReturn(Map.of("id", 1L, "permissions", List.of("READ", "WRITE")));

        // when
        String token = jwtService.generateToken(tokenData);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Should parse valid JWT token")
    void shouldParseValidJwtToken() {
        // given
        String token = jwtService.generateToken(new TokenData(1L, Set.of("READ")));
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        when(mapper.convertValue(claims, TokenData.class)).thenReturn(new TokenData(1L, Set.of("READ")));

        // when
        TokenData tokenData = jwtService.parseToken(token);

        // then
        assertNotNull(tokenData);
        assertEquals(1L, tokenData.getId());
        assertEquals(Set.of("READ"), tokenData.getPermissions());
    }

    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        // given
        ReflectionTestUtils.setField(jwtService, "expirationTime", -1L);
        String token = jwtService.generateToken(new TokenData(1L, Set.of("READ")));

        // when
        boolean isExpired = jwtService.isExpired(token);

        // then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Should throw exception for invalid token")
    void shouldThrowExceptionForInvalidToken() {
        // given
        String invalidToken = "invalid token payload";

        // when
        // then
        assertThrows(InvalidTokenException.class, () -> jwtService.isExpired(invalidToken));
    }
}
