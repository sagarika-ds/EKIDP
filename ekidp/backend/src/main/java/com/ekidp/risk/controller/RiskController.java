package com.ekidp.risk.controller;

import com.ekidp.risk.dto.RiskResponse;
import com.ekidp.risk.service.RiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/employee/{email}")
    public ResponseEntity<RiskResponse> getEmployeeRisk(
            @PathVariable String email) {
        return ResponseEntity.ok(
                riskService.calculateEmployeeRisk(email));
    }

    @GetMapping("/project/{projectName}")
    public ResponseEntity<RiskResponse> getProjectRisk(
            @PathVariable String projectName) {
        return ResponseEntity.ok(
                riskService.calculateProjectRisk(projectName));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(
                riskService.getOrganizationRiskDashboard());
    }

    @GetMapping("/gaps/{department}")
    public ResponseEntity<Map<String, Object>> getKnowledgeGaps(
            @PathVariable String department) {
        return ResponseEntity.ok(
                riskService.getKnowledgeGapAnalysis(department));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Risk service is running");
    }
}
