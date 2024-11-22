package ua.edu.internship.user.service.utils.validation.annotations.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Password {
    String message() default "Invalid password: should have minimum eight characters, at least one letter and one number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
