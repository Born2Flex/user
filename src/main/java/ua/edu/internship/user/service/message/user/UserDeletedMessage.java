package ua.edu.internship.user.service.message.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.edu.internship.user.service.enumeration.MessageType;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UserDeletedMessage extends BaseUserMessage {
    public UserDeletedMessage(String email, String fullName) {
        super(email, fullName, MessageType.USER_DELETED);
    }
}
