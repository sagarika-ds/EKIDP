package com.ekidp.knowledge.service;

import com.ekidp.knowledge.dto.DocumentResponse;
import com.ekidp.knowledge.entity.Document;
import com.ekidp.knowledge.repository.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final Tika tika;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        this.tika = new Tika();
    }

    public DocumentResponse uploadDocument(
            MultipartFile file,
            String title,
            String department,
            String category,
            String uploadedBy) {

        try {
            String extractedText = extractText(file);
            String summary = generateSummary(extractedText);
            String fileType = tika.detect(file.getOriginalFilename());

            Document document = new Document();
            document.setTitle(title != null ? title : file.getOriginalFilename());
            document.setFileName(file.getOriginalFilename());
            document.setFileType(fileType);
            document.setFileSize(file.getSize());
            document.setExtractedText(extractedText);
            document.setSummary(summary);
            document.setDepartment(department);
            document.setCategory(category);
            document.setUploadedBy(uploadedBy);

            Document saved = documentRepository.save(document);
            return toResponse(saved, "Document uploaded successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to process document: " + e.getMessage());
        }
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(doc -> toResponse(doc, null))
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        return toResponse(doc, null);
    }

    public List<DocumentResponse> searchDocuments(String keyword) {
        return documentRepository.searchByKeyword(keyword)
                .stream()
                .map(doc -> toResponse(doc, null))
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> getDocumentsByDepartment(String department) {
        return documentRepository.findByDepartment(department)
                .stream()
                .map(doc -> toResponse(doc, null))
                .collect(Collectors.toList());
    }

    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        doc.setStatus("DELETED");
        documentRepository.save(doc);
    }

    private String extractText(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY,
                    file.getOriginalFilename());
            ParseContext context = new ParseContext();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(stream, handler, metadata, context);
            return handler.toString().trim();
        } catch (Exception e) {
            return "Text extraction failed: " + e.getMessage();
        }
    }

    private String generateSummary(String text) {
        if (text == null || text.isEmpty()) return "No content extracted";
        String[] sentences = text.split("[.!?]");
        StringBuilder summary = new StringBuilder();
        int count = 0;
        for (String sentence : sentences) {
            if (count >= 3) break;
            String trimmed = sentence.trim();
            if (trimmed.length() > 20) {
                summary.append(trimmed).append(". ");
                count++;
            }
        }
        return summary.toString().trim();
    }

    private DocumentResponse toResponse(Document doc, String message) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .extractedText(doc.getExtractedText())
                .summary(doc.getSummary())
                .department(doc.getDepartment())
                .uploadedBy(doc.getUploadedBy())
                .category(doc.getCategory())
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .message(message)
                .build();
    }
}
