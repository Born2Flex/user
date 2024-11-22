package ua.edu.internship.user.service.utils.exceptions;

import org.springframework.http.HttpStatus;

public class NoSuchEntityException extends BaseException {
    public NoSuchEntityException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
