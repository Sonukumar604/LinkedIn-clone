package com.LinkedInProject.connectionsService.repository;

import com.LinkedInProject.connectionsService.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Optional<Person> findByUserId(Long userId);

    @Query("""
        MATCH (personA:Person)-[:CONNECTED_TO]-(personB:Person)
        WHERE personA.userId = $userId
        RETURN DISTINCT personB
        """)
    List<Person> getFirstDegreeConnections(@Param("userId") Long userId);

    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        RETURN count(r) > 0
        """)
    boolean connectionRequestExists(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("""
        MATCH (p1:Person)-[r:CONNECTED_TO]-(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        RETURN count(r) > 0
        """)
    boolean alreadyConnected(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("""
        MATCH (p1:Person), (p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        CREATE (p1)-[:REQUESTED_TO]->(p2)
        """)
    void addConnectionRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        DELETE r
        CREATE (p1)-[:CONNECTED_TO]->(p2)
        """)
    void acceptConnectionRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        DELETE r
        """)
    void rejectConnectionRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}