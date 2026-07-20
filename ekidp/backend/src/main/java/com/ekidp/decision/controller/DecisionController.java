package com.ekidp.decision.controller;

import com.ekidp.decision.dto.DecisionRequest;
import com.ekidp.decision.dto.DecisionResponse;
import com.ekidp.decision.service.DecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decision")
@CrossOrigin(origins = "*")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<DecisionResponse> analyzeDecision(
            @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(
                decisionService.makeDecision(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Decision Intelligence service is running");
    }
}
