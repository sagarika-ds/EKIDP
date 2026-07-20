package com.ekidp.predictive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class PredictiveService {

    private final WebClient webClient;

    @Value("${ekidp.nlp.service-url}")
    private String nlpServiceUrl;

    public PredictiveService() {
        this.webClient = WebClient.builder().build();
    }

    public Map<String, Object> predictProjectSuccess(
            double teamSize, double docCoverage, double riskScore,
            double budgetHealth, double experienceYears) {

        Map<String, Object> body = Map.of(
                "team_size", teamSize,
                "doc_coverage", docCoverage,
                "risk_score", riskScore,
                "budget_health", budgetHealth,
                "experience_years", experienceYears
        );

        return callPredictionService("/predict/project-success", body);
    }

    public Map<String, Object> predictAttrition(
            double tenureYears, double projectCount, double riskScore,
            double satisfaction, double workload) {

        Map<String, Object> body = Map.of(
                "tenure_years", tenureYears,
                "project_count", projectCount,
                "risk_score", riskScore,
                "satisfaction", satisfaction,
                "workload", workload
        );

        return callPredictionService("/predict/attrition", body);
    }

    public Map<String, Object> predictCostOverrun(
            double complexity, double teamExperience, double scopeChanges,
            double timelinePressure, double vendorDependency) {

        Map<String, Object> body = Map.of(
                "project_complexity", complexity,
                "team_experience", teamExperience,
                "scope_changes", scopeChanges,
                "timeline_pressure", timelinePressure,
                "vendor_dependency", vendorDependency
        );

        return callPredictionService("/predict/cost-overrun", body);
    }

    public Map<String, Object> predictDeliveryDelay(
            double teamSize, double docCoverage, double riskScore,
            double scopeChanges, double timelinePressure) {

        Map<String, Object> body = Map.of(
                "team_size", teamSize,
                "doc_coverage", docCoverage,
                "risk_score", riskScore,
                "scope_changes", scopeChanges,
                "timeline_pressure", timelinePressure
        );

        return callPredictionService("/predict/delivery-delay", body);
    }

    private Map<String, Object> callPredictionService(
            String endpoint, Map<String, Object> body) {
        try {
            return webClient.post()
                    .uri(nlpServiceUrl + endpoint)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return Map.of(
                    "error", "Prediction service unavailable",
                    "message", e.getMessage()
            );
        }
    }
}
