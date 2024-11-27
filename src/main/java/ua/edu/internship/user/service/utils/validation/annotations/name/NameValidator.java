package ua.edu.internship.user.service.utils.validation.annotations.name;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<Name, String> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-z]{2,}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && NAME_PATTERN.matcher(value).matches();
    }
}
