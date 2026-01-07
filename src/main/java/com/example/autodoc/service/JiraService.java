package com.example.autodoc.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JiraService {

    private final WebClient webClient;

    @Value("${jira.base-url}")
    private String baseUrl;

    @Value("${jira.email}")
    private String email;

    @Value("${jira.api-token}")
    private String apiToken;

    public JiraService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String fetchIssueRequirements(String issueKey) {
        return webClient.get()
                .uri(baseUrl + "/rest/api/3/issue/" + issueKey)
                .headers(h -> h.setBasicAuth(email, apiToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::formatJiraContext)
                .block(); // Using block for simplicity in POC; use subscribe in production
    }

    private String formatJiraContext(JsonNode node) {
        String summary = node.path("fields").path("summary").asText();

        // Jira v3 uses Atlassian Document Format (ADF) for descriptions.
        // This simple extraction gets the text content from the ADF blocks.
        StringBuilder description = new StringBuilder();
        JsonNode contentArray = node.path("fields").path("description").path("content");
        for (JsonNode content : contentArray) {
            for (JsonNode innerContent : content.path("content")) {
                description.append(innerContent.path("text").asText()).append(" ");
            }
        }

        return String.format("### JIRA REQUIREMENT: %s\n**Description:** %s",
                summary, description.toString().trim());
    }
}
