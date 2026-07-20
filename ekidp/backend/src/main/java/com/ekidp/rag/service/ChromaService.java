package com.ekidp.rag.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class ChromaService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String TENANT = "default_tenant";
    private static final String DATABASE = "default_database";
    private static final String COLLECTION_ID =
            "484aecc5-0f6f-42af-aae2-99b7aac91143";

    @Value("${ekidp.chromadb.url}")
    private String chromaUrl;

    public ChromaService() {
        this.webClient = WebClient.builder()
                .codecs(c -> c.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void addDocument(Long docId, String text, String title,
                            String department, String category) {
        try {
            List<String> chunks = chunkText(text, 500);
            List<String> ids = new ArrayList<>();
            List<String> documents = new ArrayList<>();
            List<Map<String, Object>> metadatas = new ArrayList<>();
            List<List<Double>> embeddings = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                ids.add("doc_" + docId + "_chunk_" + i);
                documents.add(chunks.get(i));
                embeddings.add(generateEmbedding(chunks.get(i)));
                Map<String, Object> meta = new HashMap<>();
                meta.put("doc_id", String.valueOf(docId));
                meta.put("title", title != null ? title : "Unknown");
                meta.put("department",
                        department != null ? department : "General");
                meta.put("category",
                        category != null ? category : "General");
                metadatas.add(meta);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("ids", ids);
            body.put("embeddings", embeddings);
            body.put("documents", documents);
            body.put("metadatas", metadatas);

            String url = chromaUrl + "/api/v2/tenants/" + TENANT +
                    "/databases/" + DATABASE + "/collections/" +
                    COLLECTION_ID + "/add";

            String response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        System.err.println("ChromaDB add error: "
                                + e.getMessage());
                        return reactor.core.publisher.Mono.just("error");
                    })
                    .block();

            System.out.println("ChromaDB add response: " + response);

        } catch (Exception e) {
            System.err.println("Error adding to ChromaDB: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> searchSimilar(String query,
                                                    int nResults) {
        try {
            List<List<Double>> queryEmbeddings = new ArrayList<>();
            queryEmbeddings.add(generateEmbedding(query));

            Map<String, Object> body = new HashMap<>();
            body.put("query_embeddings", queryEmbeddings);
            body.put("n_results", nResults);
            body.put("include", List.of("documents", "metadatas",
                    "distances"));

            String url = chromaUrl + "/api/v2/tenants/" + TENANT +
                    "/databases/" + DATABASE + "/collections/" +
                    COLLECTION_ID + "/query";

            String response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        System.err.println("ChromaDB search error: "
                                + e.getMessage());
                        return reactor.core.publisher.Mono.just("{}");
                    })
                    .block();

            System.out.println("ChromaDB search response: " + response);

            Map<String, Object> result =
                    objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> results = new ArrayList<>();

            if (result.containsKey("documents")) {
                List<List<String>> docs =
                        (List<List<String>>) result.get("documents");
                List<List<Map<String, Object>>> metas =
                        (List<List<Map<String, Object>>>) result
                                .get("metadatas");

                if (docs != null && !docs.isEmpty()) {
                    List<String> docList = docs.get(0);
                    List<Map<String, Object>> metaList =
                            metas != null && !metas.isEmpty() ?
                                    metas.get(0) : new ArrayList<>();

                    for (int i = 0; i < docList.size(); i++) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("text", docList.get(i));
                        item.put("metadata",
                                i < metaList.size() ?
                                        metaList.get(i) : new HashMap<>());
                        results.add(item);
                    }
                }
            }
            return results;

        } catch (Exception e) {
            System.err.println("Error searching ChromaDB: "
                    + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Double> generateEmbedding(String text) {
        int size = 5;
        List<Double> embedding = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        double[] values = new double[size];
        for (int i = 0; i < words.length; i++) {
            values[i % size] += words[i].hashCode() / 1000000.0;
        }
        double magnitude = 0;
        for (double v : values) magnitude += v * v;
        magnitude = Math.sqrt(magnitude);
        if (magnitude == 0) magnitude = 1;
        for (double v : values) {
            embedding.add(v / magnitude);
        }
        return embedding;
    }

    public void ensureCollectionExists() {}

    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) return chunks;
        String[] sentences = text.split("[.!?]");
        StringBuilder current = new StringBuilder();
        for (String sentence : sentences) {
            if (current.length() + sentence.length() > chunkSize
                    && current.length() > 0) {
                chunks.add(current.toString().trim());
                current = new StringBuilder();
            }
            current.append(sentence).append(". ");
        }
        if (current.length() > 0) chunks.add(current.toString().trim());
        if (chunks.isEmpty()) chunks.add(text);
        return chunks;
    }
}
