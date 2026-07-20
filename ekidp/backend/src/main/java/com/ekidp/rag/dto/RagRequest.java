package com.ekidp.rag.dto;

public class RagRequest {
    private String question;
    private String department;
    private int maxResults = 5;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
}
