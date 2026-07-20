package com.ekidp.knowledge.dto;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String extractedText;
    private String summary;
    private String department;
    private String uploadedBy;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private String message;

    public DocumentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static DocumentResponseBuilder builder() { return new DocumentResponseBuilder(); }

    public static class DocumentResponseBuilder {
        private final DocumentResponse response = new DocumentResponse();

        public DocumentResponseBuilder id(Long id) { response.id = id; return this; }
        public DocumentResponseBuilder title(String title) { response.title = title; return this; }
        public DocumentResponseBuilder fileName(String fileName) { response.fileName = fileName; return this; }
        public DocumentResponseBuilder fileType(String fileType) { response.fileType = fileType; return this; }
        public DocumentResponseBuilder fileSize(Long fileSize) { response.fileSize = fileSize; return this; }
        public DocumentResponseBuilder extractedText(String extractedText) { response.extractedText = extractedText; return this; }
        public DocumentResponseBuilder summary(String summary) { response.summary = summary; return this; }
        public DocumentResponseBuilder department(String department) { response.department = department; return this; }
        public DocumentResponseBuilder uploadedBy(String uploadedBy) { response.uploadedBy = uploadedBy; return this; }
        public DocumentResponseBuilder category(String category) { response.category = category; return this; }
        public DocumentResponseBuilder status(String status) { response.status = status; return this; }
        public DocumentResponseBuilder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public DocumentResponseBuilder message(String message) { response.message = message; return this; }
        public DocumentResponse build() { return response; }
    }
}
