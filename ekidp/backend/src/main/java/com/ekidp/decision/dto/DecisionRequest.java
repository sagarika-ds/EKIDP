package com.ekidp.decision.dto;

public class DecisionRequest {
    private String question;
    private String context;
    private String department;
    private String decisionType;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
}
