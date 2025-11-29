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
import java.util.List;

@Service
public class ExplanationService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${openai.apiKey:}")
    private String apiKey;

    @Value("${openai.chatModel:gpt-4o-mini}")
    private String chatModel;

    public String explainFit(String resumeText, String jdText, String candidateName) {
        try {
            ChatMessage system = new ChatMessage("system", "You are an AI recruiter assistant explaining candidate fit clearly.");
            ChatMessage user = new ChatMessage("user", "Job description:\n" + jdText + "\nResume for " + candidateName + ":\n" + resumeText + "\nDescribe why this candidate fits and any gaps.");
            String payload = mapper.writeValueAsString(new ChatRequest(chatModel, List.of(system, user)));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Explanation not available (check OpenAI API key).";
        }
    }

    private record ChatRequest(String model, List<ChatMessage> messages) {}
    private record ChatMessage(String role, String content) {}
}
