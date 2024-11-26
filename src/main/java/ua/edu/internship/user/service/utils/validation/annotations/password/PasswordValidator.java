package ua.edu.internship.user.service.utils.validation.annotations.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#?!@$%^&*-])[A-Za-z0-9#?!@$%^&*-]{8,}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PASSWORD_PATTERN.matcher(value).matches();
    }
}
