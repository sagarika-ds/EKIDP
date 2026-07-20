package com.ekidp.rag.controller;

import com.ekidp.rag.dto.RagRequest;
import com.ekidp.rag.dto.RagResponse;
import com.ekidp.rag.service.ChromaService;
import com.ekidp.rag.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RagController {

    private final RagService ragService;
    private final ChromaService chromaService;

    public RagController(RagService ragService,
                         ChromaService chromaService) {
        this.ragService = ragService;
        this.chromaService = chromaService;
    }

    @PostMapping("/search")
    public ResponseEntity<RagResponse> search(
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.search(request));
    }

    @PostMapping("/index")
    public ResponseEntity<Map<String, String>> indexDocument(
            @RequestParam Long docId,
            @RequestParam String text,
            @RequestParam String title,
            @RequestParam(defaultValue = "General") String department,
            @RequestParam(defaultValue = "General") String category) {

        ragService.indexDocument(docId, text, title, department, category);
        return ResponseEntity.ok(Map.of(
                "message", "Document indexed successfully",
                "docId", String.valueOf(docId)
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG service is running");
    }
}
