package com.LinkedInProject.connectionsService.controller;

import com.LinkedInProject.connectionsService.entity.Person;
import com.LinkedInProject.connectionsService.service.ConnectionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
public class ConnectionsController {
    private static final Logger log = LoggerFactory.getLogger(ConnectionsController.class);

    private final ConnectionsService connectionsService;

    public ConnectionsController(ConnectionsService connectionsService) {
        this.connectionsService = connectionsService;
    }

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>> getFirstDegreeConnections(@PathVariable Long userId){

        List<Person> personList = connectionsService.getFirstDegreeConnectionsOfUser(userId);
        return ResponseEntity.ok(personList);

    }
}
