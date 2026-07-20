package com.ekidp.graph.repository;

import com.ekidp.graph.entity.TechnologyNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyNodeRepository
        extends Neo4jRepository<TechnologyNode, Long> {

    Optional<TechnologyNode> findByName(String name);
}
