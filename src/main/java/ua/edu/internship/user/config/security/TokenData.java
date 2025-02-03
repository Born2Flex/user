package ua.edu.internship.user.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenData {
    private Long id;
    private Set<String> permissions;
}
