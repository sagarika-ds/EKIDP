package com.ekidp.rag.service;

import com.ekidp.knowledge.repository.DocumentRepository;
import com.ekidp.rag.dto.RagRequest;
import com.ekidp.rag.dto.RagResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final ChromaService chromaService;
    private final DocumentRepository documentRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ekidp.openai.api-key:}")
    private String openAiKey;

    @Value("${ekidp.anthropic.api-key:}")
    private String anthropicKey;

    public RagService(ChromaService chromaService,
                      DocumentRepository documentRepository) {
        this.chromaService = chromaService;
        this.documentRepository = documentRepository;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public RagResponse search(RagRequest request) {
        try {
            List<Map<String, Object>> results =
                    chromaService.searchSimilar(request.getQuestion(),
                            request.getMaxResults());

            List<String> relevantChunks = results.stream()
                    .map(r -> (String) r.get("text"))
                    .filter(t -> t != null && !t.isEmpty())
                    .collect(Collectors.toList());

            List<String> sourceDocs = results.stream()
                    .map(r -> {
                        Map<String, Object> meta =
                                (Map<String, Object>) r.get("metadata");
                        if (meta != null && meta.get("title") != null) {
                            return (String) meta.get("title");
                        }
                        return "Unknown Document";
                    })
                    .distinct()
                    .collect(Collectors.toList());

            String answer;
            String model;

            if (openAiKey != null && !openAiKey.isEmpty()
                    && !openAiKey.equals("YOUR_OPENAI_KEY")) {
                answer = callOpenAI(request.getQuestion(), relevantChunks);
                model = "gpt-4o";
            } else if (anthropicKey != null && !anthropicKey.isEmpty()
                    && !anthropicKey.equals("YOUR_CLAUDE_KEY")) {
                answer = callClaude(request.getQuestion(), relevantChunks);
                model = "claude-sonnet-4-6";
            } else {
                answer = generateLocalAnswer(request.getQuestion(),
                        relevantChunks);
                model = "local-rag";
            }

            double confidence = relevantChunks.isEmpty() ? 0.0 :
                    Math.min(1.0, relevantChunks.size() * 0.2);

            return RagResponse.builder()
                    .question(request.getQuestion())
                    .answer(answer)
                    .sourceDocs(sourceDocs)
                    .relevantChunks(relevantChunks)
                    .confidenceScore(confidence)
                    .model(model)
                    .build();

        } catch (Exception e) {
            return RagResponse.builder()
                    .question(request.getQuestion())
                    .answer("Error processing request: " + e.getMessage())
                    .sourceDocs(new ArrayList<>())
                    .relevantChunks(new ArrayList<>())
                    .confidenceScore(0.0)
                    .model("error")
                    .build();
        }
    }

    public void indexDocument(Long docId, String text,
                              String title, String department,
                              String category) {
        chromaService.addDocument(docId, text, title, department, category);
    }

    private String callOpenAI(String question, List<String> chunks) {
        try {
            String context = String.join("\n\n", chunks);
            String prompt = "Based on the following documents from our knowledge base, " +
                    "answer the question.\n\nContext:\n" + context +
                    "\n\nQuestion: " + question +
                    "\n\nProvide a detailed, accurate answer based only on the context provided.";

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o");
            body.put("messages", List.of(message));
            body.put("max_tokens", 1000);

            String response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + openAiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> msg =
                    (Map<String, Object>) choices.get(0).get("message");
            return (String) msg.get("content");

        } catch (Exception e) {
            return generateLocalAnswer(question, chunks);
        }
    }

    private String callClaude(String question, List<String> chunks) {
        try {
            String context = String.join("\n\n", chunks);
            String prompt = "Based on the following documents from our knowledge base, " +
                    "answer the question.\n\nContext:\n" + context +
                    "\n\nQuestion: " + question;

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "claude-sonnet-4-6");
            body.put("max_tokens", 1000);
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

            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> content =
                    (List<Map<String, Object>>) result.get("content");
            return (String) content.get(0).get("text");

        } catch (Exception e) {
            return generateLocalAnswer(question, chunks);
        }
    }

    private String generateLocalAnswer(String question,
                                       List<String> chunks) {
        if (chunks.isEmpty()) {
            return "No relevant documents found in the knowledge base " +
                    "for your question: " + question +
                    ". Please upload relevant documents first.";
        }

        StringBuilder answer = new StringBuilder();
        answer.append("Based on the knowledge base, here is what I found:\n\n");

        for (int i = 0; i < Math.min(3, chunks.size()); i++) {
            answer.append("• ").append(chunks.get(i)).append("\n\n");
        }

        answer.append("\nThis answer was generated from ")
                .append(chunks.size())
                .append(" relevant document chunks.");

        return answer.toString();
    }
}
