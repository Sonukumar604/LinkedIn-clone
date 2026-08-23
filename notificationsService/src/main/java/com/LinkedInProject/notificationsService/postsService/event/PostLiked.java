package com.LinkedInProject.notificationsService.postsService.event;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLiked {
    private Long postId;
    private Long ownerUserId;
    private Long likedByUserId;
}
