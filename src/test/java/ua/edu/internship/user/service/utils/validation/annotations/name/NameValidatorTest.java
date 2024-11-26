package ua.edu.internship.user.service.utils.validation.annotations.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NameValidatorTest {

    private final NameValidator underTest = new NameValidator();
    @Mock
    private ConstraintValidatorContext context;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/validator/name/valid-names.csv", numLinesToSkip = 1)
    void testValidNames(String name) {
        assertTrue(underTest.isValid(name, context));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/data/validator/name/invalid-names.csv", numLinesToSkip = 1)
    void testInvalidNames(String name, String message) {
        assertFalse(underTest.isValid(name, context), message);
    }
}
