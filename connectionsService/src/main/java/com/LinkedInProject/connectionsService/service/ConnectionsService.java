package com.LinkedInProject.connectionsService.service;

import com.LinkedInProject.connectionsService.entity.Person;
import com.LinkedInProject.connectionsService.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionsService {
    private static final Logger log = LoggerFactory.getLogger(ConnectionsService.class);

    private final PersonRepository personRepository;

    public ConnectionsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Fetching first-degree connections for user with ID: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }
}
