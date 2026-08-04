package com.LinkedInProject.connectionsService.service;

import com.LinkedInProject.connectionsService.entity.Person;
import com.LinkedInProject.connectionsService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsService {
    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Fetching first-degree connections for user with ID: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }
}
