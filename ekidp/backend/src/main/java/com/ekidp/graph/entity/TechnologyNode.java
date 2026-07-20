package com.ekidp.graph.entity;

import org.springframework.data.neo4j.core.schema.*;

@Node("Technology")
public class TechnologyNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("category")
    private String category;

    @Property("riskLevel")
    private String riskLevel;

    public TechnologyNode() {}

    public TechnologyNode(String name, String category) {
        this.name = name;
        this.category = category;
        this.riskLevel = "LOW";
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
