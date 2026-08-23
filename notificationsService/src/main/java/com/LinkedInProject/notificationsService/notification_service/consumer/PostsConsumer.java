package com.LinkedInProject.notificationsService.notification_service.consumer;

import com.LinkedInProject.notificationsService.notification_service.entity.Notification;
import com.LinkedInProject.notificationsService.notification_service.service.NotificationService;
import com.LinkedInProject.notificationsService.postsService.event.PostCreated;
import com.LinkedInProject.notificationsService.postsService.event.PostLiked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "post_created_topic",
            properties = {
                    JsonDeserializer.VALUE_DEFAULT_TYPE + "=com.LinkedInProject.notificationsService.postsService.event.PostCreated"
            }
    )
    public void handlePostCreated(PostCreated postCreated) {
        if (postCreated == null) {
            log.warn("Skipping null payload from post_created_topic.");
            return;
        }

        log.info("Received PostCreated event: {}", postCreated);

        String message = String.format("Your connection with id: %d created a new post: %s",
                postCreated.getOwnerUserId(), postCreated.getContent());

        Notification notification = Notification.builder()
                .message(message)
                .userId(postCreated.getUserId())
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(
            topics = "post_liked_topic",
            properties = {
                    JsonDeserializer.VALUE_DEFAULT_TYPE + "=com.LinkedInProject.notificationsService.postsService.event.PostLiked"
            }
    )
    public void handlePostLiked(PostLiked postLiked) {
        if (postLiked == null) {
            log.warn("Skipping null payload from post_liked_topic.");
            return;
        }

        log.info("Received PostLiked event: {}", postLiked);

        String message = String.format("Your connection with id: %d liked your post with id: %d",
                postLiked.getOwnerUserId(), postLiked.getPostId());

        Notification notification = Notification.builder()
                .message(message)
                .userId(postLiked.getOwnerUserId())
                .build();

        notificationService.addNotification(notification);
    }
}