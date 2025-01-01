package ua.edu.internship.user.service.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.internship.user.config.security.TokenData;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-time-minutes}")
    private long expirationTime;
    private final ObjectMapper mapper;
    public static final TypeReference<Map<String, Object>> TOKEN_DATA_TYPE_REF = new TypeReference<>() {};

    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            throw e;
        }
    }

    public TokenData parseToken(String token) {
        Claims claims = getClaims(token);
        return mapper.convertValue(claims, TokenData.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(TokenData tokenData) {
        Date issuedDateTime = new Date(System.currentTimeMillis());
        Date expirationDateTime = new Date(System.currentTimeMillis() + expirationTime * 60 * 1000);
        return Jwts.builder()
                .claims(mapper.convertValue(tokenData, TOKEN_DATA_TYPE_REF))
                .issuedAt(issuedDateTime)
                .expiration(expirationDateTime)
                .signWith(getSigningKey()).compact();
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
