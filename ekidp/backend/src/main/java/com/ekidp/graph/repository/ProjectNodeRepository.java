package com.ekidp.graph.repository;

import com.ekidp.graph.entity.ProjectNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectNodeRepository
        extends Neo4jRepository<ProjectNode, Long> {

    Optional<ProjectNode> findByName(String name);

    List<ProjectNode> findByDepartment(String department);

    List<ProjectNode> findByStatus(String status);

    @Query("MATCH (p:Project)-[:USES]->(t:Technology) " +
           "WHERE t.name = $techName RETURN p")
    List<ProjectNode> findByTechnology(String techName);
}
