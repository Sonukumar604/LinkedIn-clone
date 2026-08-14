package com.LinkedInProject.notificationsService.repository;

import com.LinkedInProject.notificationsService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
