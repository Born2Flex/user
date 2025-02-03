package ua.edu.internship.user.service.enumeration;

import lombok.Getter;

@Getter
public enum MessageType {
    USER_REGISTERED("user-registered"),
    USER_DELETED("user-deleted"),
    INTERVIEW_SCHEDULED("interview-scheduled");

    private final String templateName;

    MessageType(String templateName) {
        this.templateName = templateName;
    }
}
