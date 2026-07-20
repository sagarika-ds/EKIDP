package com.ekidp.rag.dto;

import java.util.List;

public class RagResponse {
    private String question;
    private String answer;
    private List<String> sourceDocs;
    private List<String> relevantChunks;
    private double confidenceScore;
    private String model;

    public RagResponse() {}

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public List<String> getSourceDocs() { return sourceDocs; }
    public void setSourceDocs(List<String> sourceDocs) { this.sourceDocs = sourceDocs; }
    public List<String> getRelevantChunks() { return relevantChunks; }
    public void setRelevantChunks(List<String> relevantChunks) { this.relevantChunks = relevantChunks; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public static RagResponseBuilder builder() { return new RagResponseBuilder(); }

    public static class RagResponseBuilder {
        private final RagResponse r = new RagResponse();
        public RagResponseBuilder question(String q) { r.question = q; return this; }
        public RagResponseBuilder answer(String a) { r.answer = a; return this; }
        public RagResponseBuilder sourceDocs(List<String> s) { r.sourceDocs = s; return this; }
        public RagResponseBuilder relevantChunks(List<String> c) { r.relevantChunks = c; return this; }
        public RagResponseBuilder confidenceScore(double s) { r.confidenceScore = s; return this; }
        public RagResponseBuilder model(String m) { r.model = m; return this; }
        public RagResponse build() { return r; }
    }
}
