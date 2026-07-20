package com.ekidp.graph.repository;

import com.ekidp.graph.entity.EmployeeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeNodeRepository
        extends Neo4jRepository<EmployeeNode, Long> {

    Optional<EmployeeNode> findByEmail(String email);

    List<EmployeeNode> findByDepartment(String department);

    @Query("MATCH (e:Employee) WHERE e.riskScore > $threshold RETURN e")
    List<EmployeeNode> findHighRiskEmployees(double threshold);

    @Query("MATCH (e:Employee)-[:WORKS_ON]->(p:Project) " +
           "WHERE p.name = $projectName RETURN e")
    List<EmployeeNode> findByProject(String projectName);

    @Query("MATCH (e:Employee)-[:KNOWS]->(t:Technology) " +
           "WHERE t.name = $techName RETURN e")
    List<EmployeeNode> findByTechnology(String techName);
}
