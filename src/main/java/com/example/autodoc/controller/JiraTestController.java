package com.example.autodoc.controller;

import com.example.autodoc.service.JiraTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/jira")
public class JiraTestController {

    private final JiraTestService jiraTestService;

    public JiraTestController(JiraTestService jiraTestService) {
        this.jiraTestService = jiraTestService;
    }

    /**
     * Checks if the connection to Jira is valid by fetching stories.
     * Use your project key (e.g., "STORY" or "PROJ").
     */
    @GetMapping("/stories/{projectKey}")
    public ResponseEntity<?> testJiraConnection(@PathVariable String projectKey) {
        try {
            List<Map<String, String>> stories = jiraTestService.fetchAllStories(projectKey);

            if (stories.isEmpty()) {
                return ResponseEntity.ok("Connection successful, but no stories found in project: " + projectKey);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "Success",
                    "message", "Connected to Jira successfully",
                    "count", stories.size(),
                    "data", stories
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "Error",
                    "message", "Failed to connect to Jira: " + e.getMessage()
            ));
        }
    }
}
