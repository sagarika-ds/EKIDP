package com.ekidp.graph.entity;

import org.springframework.data.neo4j.core.schema.*;
import java.util.HashSet;
import java.util.Set;

@Node("Project")
public class ProjectNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("department")
    private String department;

    @Property("status")
    private String status;

    @Property("successProbability")
    private Double successProbability;

    @Relationship(type = "USES", direction = Relationship.Direction.OUTGOING)
    private Set<TechnologyNode> technologies = new HashSet<>();

    public ProjectNode() {}

    public ProjectNode(String name, String department, String status) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.successProbability = 0.0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getSuccessProbability() { return successProbability; }
    public void setSuccessProbability(Double p) { this.successProbability = p; }
    public Set<TechnologyNode> getTechnologies() { return technologies; }
    public void setTechnologies(Set<TechnologyNode> t) { this.technologies = t; }
}
