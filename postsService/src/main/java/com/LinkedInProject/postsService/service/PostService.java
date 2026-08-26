package com.LinkedInProject.postsService.service;

import com.LinkedInProject.postsService.auth.AuthContextHolder;
import com.LinkedInProject.postsService.client.ConnectionsServiceClient;
import com.LinkedInProject.postsService.client.UploaderServiceClient;
import com.LinkedInProject.postsService.dto.PersonDto;
import com.LinkedInProject.postsService.dto.PostCreateRequestDto;
import com.LinkedInProject.postsService.dto.PostDto;
import com.LinkedInProject.postsService.entity.Post;
import com.LinkedInProject.postsService.event.PostCreated;
import com.LinkedInProject.postsService.exception.ResourceNotFoundException;
import com.LinkedInProject.postsService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, MultipartFile file) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("Creating post for user with id: {}", userId);

        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(file);

        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());
        post = postRepository.save(post);
        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);

        for(PersonDto person: personDtoList){
            PostCreated postCreated = PostCreated.builder()
                    .postId(post.getId())
                    .userId(person.getUserId())
                    .ownerUserId(userId)
                    .content(post.getContent())
                    .build();
            postCreatedKafkaTemplate.send("post_created_topic", postCreated);
        }
        return modelMapper.map(post, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Getting post with ID: {}", postId);
        Long userId = AuthContextHolder.getCurrentUserId();
        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        boolean isOwnPost = post.getUserId().equals(userId);
        boolean isFirstDegreePost = personDtoList.stream()
                .anyMatch(personDto -> personDto.getUserId().equals(post.getUserId()));
        if (!isOwnPost && !isFirstDegreePost) {
            throw new ResourceNotFoundException("Post not found with ID: " + postId);
        }
        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all the posts of a user with ID: {}", userId);
        List<Post> postList = postRepository.findByUserId(userId);
        return postList.stream()
                .map((element) -> modelMapper.map(element, PostDto.class))
                .collect(Collectors.toList());
    }
}
