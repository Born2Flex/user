package ua.edu.internship.user.service.utils.exceptions;

import org.springframework.http.HttpStatus;

public class EmailDuplicateException extends BaseException {
    public EmailDuplicateException() {
        super("User with such email already exists", HttpStatus.CONFLICT);
    }
}
