package com.ekidp.predictive.controller;

import com.ekidp.predictive.service.PredictiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/predict")
@CrossOrigin(origins = "*")
public class PredictiveController {

    private final PredictiveService predictiveService;

    public PredictiveController(PredictiveService predictiveService) {
        this.predictiveService = predictiveService;
    }

    @PostMapping("/project-success")
    public ResponseEntity<Map<String, Object>> predictSuccess(
            @RequestParam double teamSize,
            @RequestParam double docCoverage,
            @RequestParam double riskScore,
            @RequestParam double budgetHealth,
            @RequestParam double experienceYears) {
        return ResponseEntity.ok(predictiveService.predictProjectSuccess(
                teamSize, docCoverage, riskScore,
                budgetHealth, experienceYears));
    }

    @PostMapping("/attrition")
    public ResponseEntity<Map<String, Object>> predictAttrition(
            @RequestParam double tenureYears,
            @RequestParam double projectCount,
            @RequestParam double riskScore,
            @RequestParam double satisfaction,
            @RequestParam double workload) {
        return ResponseEntity.ok(predictiveService.predictAttrition(
                tenureYears, projectCount, riskScore,
                satisfaction, workload));
    }

    @PostMapping("/cost-overrun")
    public ResponseEntity<Map<String, Object>> predictCostOverrun(
            @RequestParam double complexity,
            @RequestParam double teamExperience,
            @RequestParam double scopeChanges,
            @RequestParam double timelinePressure,
            @RequestParam double vendorDependency) {
        return ResponseEntity.ok(predictiveService.predictCostOverrun(
                complexity, teamExperience, scopeChanges,
                timelinePressure, vendorDependency));
    }

    @PostMapping("/delivery-delay")
    public ResponseEntity<Map<String, Object>> predictDelay(
            @RequestParam double teamSize,
            @RequestParam double docCoverage,
            @RequestParam double riskScore,
            @RequestParam double scopeChanges,
            @RequestParam double timelinePressure) {
        return ResponseEntity.ok(predictiveService.predictDeliveryDelay(
                teamSize, docCoverage, riskScore,
                scopeChanges, timelinePressure));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Predictive Analytics service is running");
    }
}
