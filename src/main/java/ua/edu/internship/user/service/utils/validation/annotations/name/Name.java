package ua.edu.internship.user.service.utils.validation.annotations.name;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NameValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Name {
    String message() default "Invalid name: must contain at least 3 characters and only English letters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
