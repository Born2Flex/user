package ua.edu.internship.user.service.message.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.internship.user.service.enumeration.MessageType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUserMessage {
    private String email;
    private String fullName;
    private MessageType messageType;
}
