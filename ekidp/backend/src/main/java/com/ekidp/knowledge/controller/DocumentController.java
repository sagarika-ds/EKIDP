package com.ekidp.knowledge.controller;

import com.ekidp.knowledge.dto.DocumentResponse;
import com.ekidp.knowledge.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "department", defaultValue = "General") String department,
            @RequestParam(value = "category", defaultValue = "General") String category,
            Authentication authentication) {

        String uploadedBy = authentication != null ?
                authentication.getName() : "anonymous";

        DocumentResponse response = documentService.uploadDocument(
                file, title, department, category, uploadedBy);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(documentService.searchDocuments(keyword));
    }

    @GetMapping("/documents/department/{department}")
    public ResponseEntity<List<DocumentResponse>> getByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(documentService.getDocumentsByDepartment(department));
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok("Document deleted successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Knowledge service is running");
    }
}
