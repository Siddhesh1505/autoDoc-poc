package com.example.autodoc.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JiraTestService {

    private static final Logger log = LoggerFactory.getLogger(JiraTestService.class);
    private final WebClient webClient;

    @Value("${jira.email}")
    private String email;

    @Value("${jira.api-token}")
    private String apiToken;

    /**
     * Constructor injection for WebClient.
     * We initialize the WebClient with the baseUrl here to prevent "undefined scheme" errors.
     */
    public JiraTestService(WebClient.Builder builder, @Value("${jira.base-url}") String baseUrl) {
        log.info("Initializing JiraTestService with base URL: {}", baseUrl);
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public List<Map<String, String>> fetchAllStories(String projectKey) {
        String jql = String.format("project = '%s' AND issuetype = 'Story'", projectKey);

        log.info("Fetching stories for project {} with explicit fields", projectKey);

        try {
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/3/search/jql")
                            .queryParam("jql", jql)
                            // ADD THIS LINE: Tell Jira which fields to include
                            .queryParam("fields", "summary,status,key")
                            .build())
                    .headers(h -> h.setBasicAuth(email, apiToken))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

//            System.out.println("FULL JIRA RESPONSE: " + response.toString());
            return parseStoriesFromResponse(response);
        } catch (Exception e) {
            log.error("Jira Search Failed: {}", e.getMessage());
            throw new RuntimeException("API Path Error: " + e.getMessage());
        }
    }

    /**
     * Helper to parse the complex Jira JSON response into a simple List.
     */
    private List<Map<String, String>> parseStoriesFromResponse(JsonNode response) {
        List<Map<String, String>> stories = new ArrayList<>();

//        System.out.println("FULL JIRA RESPONSE: " + response.toString());
        if (response != null && response.has("issues")) {
            JsonNode issuesArray = response.get("issues");

            for (JsonNode issue : issuesArray) {
                // 1. Key is at the root of the issue object
                String key = issue.path("key").asText("N/A");

                // 2. All other data is inside the "fields" object
                JsonNode fields = issue.path("fields");

                // 3. Summary is directly inside fields
                String summary = fields.path("summary").asText("No Summary");

                // 4. Status is an object, name is inside it
                String status = fields.path("status").path("name").asText("Unknown Status");

                stories.add(Map.of(
                        "key", key,
                        "summary", summary,
                        "status", status
                ));
            }
        }
        return stories;
    }
}