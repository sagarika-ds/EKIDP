package com.ekidp.decision.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DecisionResponse {
    private String question;
    private String executiveSummary;
    private String finalRecommendation;
    private String decision;
    private double confidenceScore;
    private List<String> pros;
    private List<String> cons;
    private List<String> risks;
    private List<String> recommendations;
    private Map<String, String> agentOutputs;
    private List<String> evidences;
    private String costAnalysis;
    private String timeline;
    private List<String> actionPlan;
    private String riskLevel;
    private LocalDateTime generatedAt;

    public DecisionResponse() {}

    public String getQuestion() { return question; }
    public void setQuestion(String q) { this.question = q; }
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String s) { this.executiveSummary = s; }
    public String getFinalRecommendation() { return finalRecommendation; }
    public void setFinalRecommendation(String r) { this.finalRecommendation = r; }
    public String getDecision() { return decision; }
    public void setDecision(String d) { this.decision = d; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double c) { this.confidenceScore = c; }
    public List<String> getPros() { return pros; }
    public void setPros(List<String> p) { this.pros = p; }
    public List<String> getCons() { return cons; }
    public void setCons(List<String> c) { this.cons = c; }
    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> r) { this.risks = r; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> r) { this.recommendations = r; }
    public Map<String, String> getAgentOutputs() { return agentOutputs; }
    public void setAgentOutputs(Map<String, String> a) { this.agentOutputs = a; }
    public List<String> getEvidences() { return evidences; }
    public void setEvidences(List<String> e) { this.evidences = e; }
    public String getCostAnalysis() { return costAnalysis; }
    public void setCostAnalysis(String c) { this.costAnalysis = c; }
    public String getTimeline() { return timeline; }
    public void setTimeline(String t) { this.timeline = t; }
    public List<String> getActionPlan() { return actionPlan; }
    public void setActionPlan(List<String> a) { this.actionPlan = a; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String r) { this.riskLevel = r; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime g) { this.generatedAt = g; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DecisionResponse r = new DecisionResponse();
        public Builder question(String v) { r.question = v; return this; }
        public Builder executiveSummary(String v) { r.executiveSummary = v; return this; }
        public Builder finalRecommendation(String v) { r.finalRecommendation = v; return this; }
        public Builder decision(String v) { r.decision = v; return this; }
        public Builder confidenceScore(double v) { r.confidenceScore = v; return this; }
        public Builder pros(List<String> v) { r.pros = v; return this; }
        public Builder cons(List<String> v) { r.cons = v; return this; }
        public Builder risks(List<String> v) { r.risks = v; return this; }
        public Builder recommendations(List<String> v) { r.recommendations = v; return this; }
        public Builder agentOutputs(Map<String, String> v) { r.agentOutputs = v; return this; }
        public Builder evidences(List<String> v) { r.evidences = v; return this; }
        public Builder costAnalysis(String v) { r.costAnalysis = v; return this; }
        public Builder timeline(String v) { r.timeline = v; return this; }
        public Builder actionPlan(List<String> v) { r.actionPlan = v; return this; }
        public Builder riskLevel(String v) { r.riskLevel = v; return this; }
        public Builder generatedAt(LocalDateTime v) { r.generatedAt = v; return this; }
        public DecisionResponse build() { return r; }
    }
}
