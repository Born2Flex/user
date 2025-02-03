package ua.edu.internship.user.service.utils.validation.annotations.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NameValidatorTest {

    private final NameValidator underTest = new NameValidator();
    @Mock
    private ConstraintValidatorContext context;

    private static String[] validNames = {
            "John",
            "Alice",
            "Sarah",
            "Emma",
            "James",
            "Mia",
            "Illya"
    };

    @ParameterizedTest
    @FieldSource("validNames")
    void testValidNames(String name) {
        assertTrue(underTest.isValid(name, context));
    }

    private static String[][] invalidNames = {
            {"john", "Name should start with an uppercase letter"},
            {"ALICE", "Name should have only the first letter as uppercase and the rest lowercase"},
            {"J", "Name should have at least 2 characters"},
            {"sarah", "Name should start with an uppercase letter"},
            {"123abc", "Name should start with an uppercase letter and contain only alphabetic characters"},
            {"emma@", "Name should only contain alphabetic characters"},
            {"m1a", "Name should contain only alphabetic characters"},
            {"123", "Name should contain only alphabetic characters"}
    };

    @ParameterizedTest(name = "{0}: {1}")
    @FieldSource("invalidNames")
    void testInvalidNames(String name, String message) {
        assertFalse(underTest.isValid(name, context), message);
    }
}
