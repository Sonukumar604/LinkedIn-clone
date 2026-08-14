package com.LinkedInProject.postsService.service;

import com.LinkedInProject.postsService.auth.AuthContextHolder;
import com.LinkedInProject.postsService.entity.Post;
import com.LinkedInProject.postsService.entity.PostLike;
import com.LinkedInProject.postsService.event.PostLiked;
import com.LinkedInProject.postsService.exception.BadRequestException;
import com.LinkedInProject.postsService.exception.ResourceNotFoundException;
import com.LinkedInProject.postsService.repository.PostLikeRepository;
import com.LinkedInProject.postsService.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    @Transactional
    public void likePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with ID:{} liking the post with ID:{}", userId, postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with ID:  " + postId));
        boolean hasAlreadyLiked = postLikeRepository.existsByIdAndPostId(userId, postId);
        if(hasAlreadyLiked) throw new BadRequestException("You cannot like the post again");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        PostLiked postLiked = PostLiked.builder()
                .postId(postId)
                .ownerUserId(post.getUserId())
                .likedByUserId(userId)
                .build();
        postLikedKafkaTemplate.send("post_liked_topics", postLiked);
    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with ID:{} unliking the post with ID:{}", userId, postId);
        postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with ID:  " + postId));
        boolean hasAlreadyLiked = postLikeRepository.existsByIdAndPostId(userId, postId);
        if(!hasAlreadyLiked) throw new BadRequestException("You cannot unlike a post you haven't liked");
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    }
}
