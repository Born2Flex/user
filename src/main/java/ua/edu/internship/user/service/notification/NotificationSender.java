package ua.edu.internship.user.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ua.edu.internship.user.service.message.user.BaseUserMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSender {
    private final JmsTemplate jmsTemplate;
    @Value("${user.queue}")
    private String userQueue;

    public void sendNotification(BaseUserMessage message) {
        log.info("Sending user notification message in queue: {}", message);
        jmsTemplate.convertAndSend(userQueue, message);
    }
}
