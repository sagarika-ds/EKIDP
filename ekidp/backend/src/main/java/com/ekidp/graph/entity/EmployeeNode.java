package com.ekidp.graph.entity;

import org.springframework.data.neo4j.core.schema.*;
import java.util.HashSet;
import java.util.Set;

@Node("Employee")
public class EmployeeNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("email")
    private String email;

    @Property("department")
    private String department;

    @Property("role")
    private String role;

    @Property("riskScore")
    private Double riskScore;

    @Relationship(type = "WORKS_ON", direction = Relationship.Direction.OUTGOING)
    private Set<ProjectNode> projects = new HashSet<>();

    @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
    private Set<TechnologyNode> technologies = new HashSet<>();

    public EmployeeNode() {}

    public EmployeeNode(String name, String email,
                        String department, String role) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
        this.riskScore = 0.0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public Set<ProjectNode> getProjects() { return projects; }
    public void setProjects(Set<ProjectNode> projects) { this.projects = projects; }
    public Set<TechnologyNode> getTechnologies() { return technologies; }
    public void setTechnologies(Set<TechnologyNode> technologies) { this.technologies = technologies; }
}
