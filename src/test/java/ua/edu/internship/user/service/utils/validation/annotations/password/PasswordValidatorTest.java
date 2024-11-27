package ua.edu.internship.user.service.utils.validation.annotations.password;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordValidatorTest {

    private final PasswordValidator underTest = new PasswordValidator();
    @Mock
    private ConstraintValidatorContext context;

    private static String[] validPasswords = {
            "Password1!",
            "Abcd1234@",
            "ValidP@ssw0rd",
            "Secure#2024",
            "StrongP@ss1",
            "Valid123$",
            "TestPass#2024",
            "Str0ngP@ss!"
    };

    @ParameterizedTest
    @FieldSource("validPasswords")
    void testValidPasswords(String password) {
        assertTrue(underTest.isValid(password, context));
    }

    private static String[][] invalidPasswords = {
            {"password", "Should be at least one uppercase letter, one digit, one special character"},
            {"12345678", "Should be at least one uppercase and lowercase letter, and special character"},
            {"Pass1", "Should be at least one special character and be at least 8 characters long"},
            {null, "Password should not be null"},
            {"", "Password should not be empty"},
            {"abcDEFGH", "Should be at least one digit and one special character"},
            {"1234!@#$", "Should be at least one uppercase and lowercase letter"},
            {"Aa1!", "Should be at least 8 characters long"}
    };

    @ParameterizedTest(name = "{0}: {1}")
    @FieldSource("invalidPasswords")
    void testInvalidPasswords(String password, String message) {
        assertFalse(underTest.isValid(password, context), message);
    }
}
