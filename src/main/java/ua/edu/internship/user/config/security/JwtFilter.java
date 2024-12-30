package ua.edu.internship.user.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.edu.internship.user.service.business.JwtService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing request on URI: {}", request.getRequestURI());
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            if (jwtService.isExpired(token)) {
                log.info("Token is expired");
                setApiErrorResponse(response, "Token expired", 6);
                return;
            }
            AuthDetails authDetails = jwtService.parseToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(authDetails, null, List.of(authDetails::getAuthority))
            );
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            setApiErrorResponse(response, "Invalid token", 7);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setApiErrorResponse(HttpServletResponse response, String message, int errorCode) throws IOException {
        ApiErrorDto apiError = new ApiErrorDto(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), message, errorCode);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
        response.getWriter().flush();
    }
}
