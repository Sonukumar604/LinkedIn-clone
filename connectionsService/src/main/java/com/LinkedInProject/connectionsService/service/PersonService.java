package com.LinkedInProject.connectionsService.service;

import com.LinkedInProject.connectionsService.entity.Person;
import com.LinkedInProject.connectionsService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    public void createPerson(Long userId, String name){
        Person person = Person.builder()
                .userId(userId)
                .name(name)
                .build();
        personRepository.save(person);
    }

}
