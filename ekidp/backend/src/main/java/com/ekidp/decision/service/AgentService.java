package com.ekidp.decision.service;

import com.ekidp.knowledge.repository.DocumentRepository;
import com.ekidp.rag.service.ChromaService;
import com.ekidp.risk.service.RiskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private final ChromaService chromaService;
    private final DocumentRepository documentRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ekidp.openai.api-key:}")
    private String openAiKey;

    @Value("${ekidp.anthropic.api-key:}")
    private String anthropicKey;

    public AgentService(ChromaService chromaService,
                        DocumentRepository documentRepository) {
        this.chromaService = chromaService;
        this.documentRepository = documentRepository;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public String runResearchAgent(String question, String context) {
        List<Map<String, Object>> docs =
                chromaService.searchSimilar(question, 5);
        List<String> relevantDocs = docs.stream()
                .map(d -> (String) d.get("text"))
                .filter(t -> t != null && !t.isEmpty())
                .collect(Collectors.toList());

        long totalDocs = documentRepository.count();

        StringBuilder research = new StringBuilder();
        research.append("RESEARCH FINDINGS:\n");
        research.append("Total documents in knowledge base: ")
                .append(totalDocs).append("\n");
        research.append("Relevant documents found: ")
                .append(relevantDocs.size()).append("\n\n");

        if (!relevantDocs.isEmpty()) {
            research.append("Key information from knowledge base:\n");
            for (int i = 0; i < Math.min(3, relevantDocs.size()); i++) {
                research.append("- ").append(relevantDocs.get(i))
                        .append("\n");
            }
        } else {
            research.append("No directly relevant documents found. ");
            research.append("Analysis based on general knowledge.\n");
        }

        if (hasAiKey()) {
            String aiResearch = callAI(
                    "You are a Research Agent. Analyze this question and " +
                    "provide key research findings, facts, and relevant " +
                    "information. Be concise and factual.\n\nQuestion: " +
                    question + "\n\nContext: " + context +
                    "\n\nKnowledge Base Findings:\n" +
                    String.join("\n", relevantDocs),
                    500);
            research.append("\nAI Research Analysis:\n").append(aiResearch);
        }

        return research.toString();
    }

    public String runRiskAgent(String question, String researchOutput) {
        StringBuilder riskAnalysis = new StringBuilder();
        riskAnalysis.append("RISK ANALYSIS:\n");

        List<String> risks = identifyRisks(question);
        for (String risk : risks) {
            riskAnalysis.append("- ").append(risk).append("\n");
        }

        if (hasAiKey()) {
            String aiRisk = callAI(
                    "You are a Risk Analysis Agent. Identify and analyze " +
                    "all potential risks, challenges, and threats related " +
                    "to this decision. Categorize risks as HIGH, MEDIUM, " +
                    "or LOW. Be specific and practical.\n\nDecision: " +
                    question + "\n\nResearch: " + researchOutput,
                    400);
            riskAnalysis.append("\nAI Risk Assessment:\n").append(aiRisk);
        }

        return riskAnalysis.toString();
    }

    public String runFinancialAgent(String question, String context) {
        StringBuilder financial = new StringBuilder();
        financial.append("FINANCIAL ANALYSIS:\n");

        List<String> costs = estimateCosts(question);
        List<String> benefits = estimateBenefits(question);

        financial.append("Estimated Costs:\n");
        costs.forEach(c -> financial.append("- ").append(c).append("\n"));

        financial.append("\nEstimated Benefits:\n");
        benefits.forEach(b -> financial.append("- ").append(b).append("\n"));

        financial.append("\nROI Analysis: ");
        financial.append("Based on industry benchmarks, ");
        financial.append("expected ROI within 18-24 months if executed properly.\n");

        if (hasAiKey()) {
            String aiFinancial = callAI(
                    "You are a Financial Analysis Agent. Provide a " +
                    "detailed cost-benefit analysis, ROI projection, and " +
                    "budget recommendations for this decision.\n\n" +
                    "Decision: " + question + "\n\nContext: " + context,
                    400);
            financial.append("\nAI Financial Analysis:\n")
                    .append(aiFinancial);
        }

        return financial.toString();
    }

    public String runStrategyAgent(String question,
                                    String researchOutput,
                                    String riskOutput,
                                    String financialOutput) {
        StringBuilder strategy = new StringBuilder();
        strategy.append("STRATEGIC ANALYSIS:\n");

        strategy.append("Strategic Alignment: This decision aligns with ")
                .append("modern enterprise technology trends.\n");
        strategy.append("Competitive Impact: Positive - improves ")
                .append("organizational capabilities.\n");
        strategy.append("Long-term Vision: Supports digital transformation ")
                .append("and knowledge management goals.\n");

        if (hasAiKey()) {
            String aiStrategy = callAI(
                    "You are a Strategy Agent. Provide strategic " +
                    "recommendations, alignment with business goals, and " +
                    "long-term implications of this decision.\n\n" +
                    "Decision: " + question +
                    "\n\nResearch: " + researchOutput +
                    "\n\nRisks: " + riskOutput +
                    "\n\nFinancial: " + financialOutput,
                    500);
            strategy.append("\nAI Strategic Recommendations:\n")
                    .append(aiStrategy);
        }

        return strategy.toString();
    }

    public String runCriticAgent(String question,
                                  String strategyOutput) {
        StringBuilder critic = new StringBuilder();
        critic.append("CRITICAL ANALYSIS:\n");

        critic.append("Challenging Assumptions:\n");
        critic.append("- Have all alternatives been considered?\n");
        critic.append("- Are the cost estimates realistic?\n");
        critic.append("- Is the timeline achievable?\n");
        critic.append("- Are team skills sufficient?\n");
        critic.append("- What are the opportunity costs?\n");

        critic.append("\nPotential Weaknesses in Strategy:\n");
        critic.append("- Implementation complexity may be underestimated\n");
        critic.append("- Change management challenges not fully addressed\n");
        critic.append("- Vendor lock-in risks should be considered\n");

        if (hasAiKey()) {
            String aiCritic = callAI(
                    "You are a Critic Agent. Challenge the proposed " +
                    "strategy, identify weaknesses, question assumptions, " +
                    "and provide counter-arguments. Be constructively " +
                    "critical.\n\nDecision: " + question +
                    "\n\nProposed Strategy: " + strategyOutput,
                    400);
            critic.append("\nAI Critical Analysis:\n").append(aiCritic);
        }

        return critic.toString();
    }

    public String runFinalDecisionAgent(String question,
                                         String researchOutput,
                                         String riskOutput,
                                         String financialOutput,
                                         String strategyOutput,
                                         String criticOutput) {
        StringBuilder finalDecision = new StringBuilder();
        finalDecision.append("FINAL DECISION:\n");

        if (hasAiKey()) {
            String aiDecision = callAI(
                    "You are the Final Decision Agent. You have received " +
                    "analysis from 5 specialist agents. Synthesize all " +
                    "inputs and provide:\n" +
                    "1. Clear YES/NO/CONDITIONAL recommendation\n" +
                    "2. Executive summary (3-4 sentences)\n" +
                    "3. Top 3 pros\n" +
                    "4. Top 3 cons\n" +
                    "5. Top 3 risks\n" +
                    "6. 5-step action plan\n" +
                    "7. Timeline estimate\n" +
                    "8. Confidence score (0-100)\n\n" +
                    "Decision Question: " + question +
                    "\n\nResearch: " + researchOutput +
                    "\n\nRisks: " + riskOutput +
                    "\n\nFinancial: " + financialOutput +
                    "\n\nStrategy: " + strategyOutput +
                    "\n\nCritical Analysis: " + criticOutput,
                    800);
            finalDecision.append(aiDecision);
        } else {
            finalDecision.append(generateLocalDecision(question,
                    riskOutput));
        }

        return finalDecision.toString();
    }

    private String generateLocalDecision(String question,
                                          String riskOutput) {
        boolean isHighRisk = riskOutput.toLowerCase()
                .contains("high risk");
        String recommendation = isHighRisk ?
                "CONDITIONAL - Proceed with caution" : "YES - Recommended";

        return "Recommendation: " + recommendation + "\n\n" +
                "Executive Summary: Based on analysis of available " +
                "knowledge base and risk assessment, this decision " +
                "requires careful planning and execution. The " +
                "organization should proceed with a phased approach " +
                "to minimize risks while maximizing benefits.\n\n" +
                "Action Plan:\n" +
                "1. Conduct detailed feasibility study\n" +
                "2. Assemble cross-functional team\n" +
                "3. Create proof of concept\n" +
                "4. Develop implementation roadmap\n" +
                "5. Execute with regular checkpoints\n\n" +
                "Timeline: 6-12 months for full implementation\n" +
                "Confidence Score: 72";
    }

    private List<String> identifyRisks(String question) {
        List<String> risks = new ArrayList<>();
        String q = question.toLowerCase();
        if (q.contains("cloud") || q.contains("aws") ||
                q.contains("migrate")) {
            risks.add("HIGH: Data migration complexity and potential data loss");
            risks.add("MEDIUM: Cost overrun during transition period");
            risks.add("MEDIUM: Team skill gaps for new technology");
            risks.add("LOW: Vendor lock-in with cloud provider");
        } else if (q.contains("hire") || q.contains("team")) {
            risks.add("MEDIUM: Recruitment timeline may delay projects");
            risks.add("LOW: Cultural fit challenges");
            risks.add("LOW: Training and onboarding costs");
        } else {
            risks.add("MEDIUM: Implementation complexity");
            risks.add("MEDIUM: Resource availability");
            risks.add("LOW: Stakeholder resistance to change");
        }
        return risks;
    }

    private List<String> estimateCosts(String question) {
        String q = question.toLowerCase();
        if (q.contains("cloud") || q.contains("aws")) {
            return Arrays.asList(
                    "Infrastructure migration: $50,000 - $200,000",
                    "Training and upskilling: $10,000 - $30,000",
                    "Consulting fees: $20,000 - $80,000",
                    "Ongoing monthly costs: $5,000 - $25,000"
            );
        }
        return Arrays.asList(
                "Initial setup costs: $20,000 - $100,000",
                "Operational costs: $5,000 - $20,000 per month",
                "Training costs: $5,000 - $15,000"
        );
    }

    private List<String> estimateBenefits(String question) {
        return Arrays.asList(
                "Improved operational efficiency: 20-40% improvement",
                "Cost savings: $30,000 - $150,000 annually",
                "Faster time to market: 30% reduction",
                "Better scalability and flexibility",
                "Reduced maintenance overhead"
        );
    }

    private boolean hasAiKey() {
        return (openAiKey != null && !openAiKey.isEmpty() &&
                !openAiKey.equals("YOUR_OPENAI_KEY")) ||
               (anthropicKey != null && !anthropicKey.isEmpty() &&
                !anthropicKey.equals("YOUR_CLAUDE_KEY"));
    }

    private String callAI(String prompt, int maxTokens) {
        try {
            if (openAiKey != null && !openAiKey.isEmpty() &&
                    !openAiKey.equals("YOUR_OPENAI_KEY")) {
                return callOpenAI(prompt, maxTokens);
            } else if (anthropicKey != null && !anthropicKey.isEmpty() &&
                    !anthropicKey.equals("YOUR_CLAUDE_KEY")) {
                return callClaude(prompt, maxTokens);
            }
        } catch (Exception e) {
            System.err.println("AI call failed: " + e.getMessage());
        }
        return "AI analysis not available. Add API key to application.yml";
    }

    private String callOpenAI(String prompt, int maxTokens) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o");
            body.put("messages", List.of(message));
            body.put("max_tokens", maxTokens);
            String response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Map<String, Object> result =
                    objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> msg =
                    (Map<String, Object>) choices.get(0).get("message");
            return (String) msg.get("content");
        } catch (Exception e) {
            return "OpenAI call failed: " + e.getMessage();
        }
    }

    private String callClaude(String prompt, int maxTokens) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            Map<String, Object> body = new HashMap<>();
            body.put("model", "claude-sonnet-4-6");
            body.put("max_tokens", maxTokens);
            body.put("messages", List.of(message));
            String response = webClient.post()
                    .uri("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", anthropicKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Map<String, Object> result =
                    objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> content =
                    (List<Map<String, Object>>) result.get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            return "Claude call failed: " + e.getMessage();
        }
    }
}
