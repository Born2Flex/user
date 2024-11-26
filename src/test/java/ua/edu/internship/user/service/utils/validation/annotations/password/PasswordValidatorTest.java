package ua.edu.internship.user.service.utils.validation.annotations.password;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordValidatorTest {

    private final PasswordValidator underTest = new PasswordValidator();
    @Mock
    private ConstraintValidatorContext context;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/validator/password/valid-passwords.csv", numLinesToSkip = 1)
    void testValidPasswords(String password) {
        assertTrue(underTest.isValid(password, context));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/data/validator/password/invalid-passwords.csv", numLinesToSkip = 1)
    void testInvalidPasswords(String password, String message) {
        assertFalse(underTest.isValid(password, context), message);
    }
}
