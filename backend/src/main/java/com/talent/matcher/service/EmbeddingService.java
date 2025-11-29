package com.talent.matcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${openai.apiKey:}")
    private String apiKey;

    @Value("${openai.embeddingModel:text-embedding-3-small}")
    private String embeddingModel;

    public List<Double> embedText(String text) {
        try {
            String body = objectMapper.writeValueAsString(new EmbeddingRequest(embeddingModel, text));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/embeddings"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode vector = root.path("data").get(0).path("embedding");
            List<Double> values = new ArrayList<>();
            vector.forEach(v -> values.add(v.asDouble()));
            return values;
        } catch (Exception e) {
            return List.of();
        }
    }

    private record EmbeddingRequest(String model, String input) {}
}
