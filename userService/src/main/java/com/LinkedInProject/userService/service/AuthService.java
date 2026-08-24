package com.LinkedInProject.userService.service;

import com.LinkedInProject.userService.dto.LoginRequestDto;
import com.LinkedInProject.userService.dto.SignupRequestDto;
import com.LinkedInProject.userService.dto.UserDto;
import com.LinkedInProject.userService.event.UserCreatedEvent;
import com.LinkedInProject.userService.exception.BadRequestException;
import com.LinkedInProject.userService.exception.ResourceNotFoundException;
import com.LinkedInProject.userService.repository.UserRepository;
import com.LinkedInProject.userService.utils.BCrypt;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate;


    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}", signupRequestDto.getEmail());
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists){
            throw new BadRequestException("User already exists");
        }
        com.LinkedInProject.userService.entity.User user = modelMapper.map(signupRequestDto, com.LinkedInProject.userService.entity.User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));
        user = userRepository.save(user);
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
        userCreatedEventKafkaTemplate.send("user_created_topic", userCreatedEvent);

        return modelMapper.map(user, UserDto.class);

    }

    public String login(LoginRequestDto loginRequestDto) {

        log.info("Login request for email: {}", loginRequestDto.getEmail());

        com.LinkedInProject.userService.entity.User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new BadRequestException("Incorrect email or password"));

        log.info("User found: {}", user.getEmail());
        log.info("Stored Password: {}", user.getPassword());

        boolean isPasswordMatch =
                BCrypt.match(loginRequestDto.getPassword(), user.getPassword());

        log.info("Password Match: {}", isPasswordMatch);

        if (!isPasswordMatch) {
            throw new BadRequestException("Incorrect email or password");
        }

        return jwtService.generateAccessToken(user);
    }
}
