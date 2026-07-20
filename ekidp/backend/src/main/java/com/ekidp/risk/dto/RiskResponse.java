package com.ekidp.risk.dto;

import java.util.List;
import java.util.Map;

public class RiskResponse {

    private String entityName;
    private String entityType;
    private double riskScore;
    private String riskLevel;
    private List<String> riskFactors;
    private List<String> recommendations;
    private Map<String, Object> details;
    private String message;

    public RiskResponse() {}

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static RiskResponseBuilder builder() { return new RiskResponseBuilder(); }

    public static class RiskResponseBuilder {
        private final RiskResponse r = new RiskResponse();
        public RiskResponseBuilder entityName(String v) { r.entityName = v; return this; }
        public RiskResponseBuilder entityType(String v) { r.entityType = v; return this; }
        public RiskResponseBuilder riskScore(double v) { r.riskScore = v; return this; }
        public RiskResponseBuilder riskLevel(String v) { r.riskLevel = v; return this; }
        public RiskResponseBuilder riskFactors(List<String> v) { r.riskFactors = v; return this; }
        public RiskResponseBuilder recommendations(List<String> v) { r.recommendations = v; return this; }
        public RiskResponseBuilder details(Map<String, Object> v) { r.details = v; return this; }
        public RiskResponseBuilder message(String v) { r.message = v; return this; }
        public RiskResponse build() { return r; }
    }
}
