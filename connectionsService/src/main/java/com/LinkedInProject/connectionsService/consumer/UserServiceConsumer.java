package com.LinkedInProject.connectionsService.consumer;

import com.LinkedInProject.connectionsService.service.PersonService;
import com.LinkedInProject.userService.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceConsumer {
    private final PersonService personService;

    @KafkaListener(
            topics = "user_created_topic",
            properties = {
                    "spring.json.value.default.type=com.LinkedInProject.userService.event.UserCreatedEvent",
                    "spring.json.trusted.packages=*"
            }
    )
    public void handlePersonCreated(@Payload UserCreatedEvent userCreatedEvent) {
        log.info("Received UserCreatedEvent: {}", userCreatedEvent);
        personService.createPerson(userCreatedEvent.getUserId(), userCreatedEvent.getName());
    }
}