package com.LinkedInProject.postsService.client;

import com.LinkedInProject.postsService.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "connections-service",
        path = "/connections",
        url = "${CONNECTIONS_SERVICE_URI:http://connections-service}"
)
public interface ConnectionsServiceClient {

    @GetMapping("/core/{userId}/first-degree")
    List<PersonDto> getFirstDegreeConnections(@PathVariable("userId") Long userId);
}