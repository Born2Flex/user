package ua.edu.internship.user.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ua.edu.internship.user.service.message.user.UserRegisteredMessage;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSenderTest {
    @Mock
    private JmsTemplate jmsTemplate;
    @InjectMocks
    private NotificationSender underTest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(underTest, "userQueue", "test-queue");
    }

    @Test
    @DisplayName("Should send notification message")
    void shouldSendNotificationMessage() {
        // given
        UserRegisteredMessage notification = new UserRegisteredMessage("email@gmail.com", "John Doe");

        // when
        underTest.sendNotification(notification);

        // then
        verify(jmsTemplate).convertAndSend("test-queue", notification);
    }
}
