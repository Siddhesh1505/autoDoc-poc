package com.example.autodoc.controller;

import com.example.autodoc.service.JiraService;
import com.example.autodoc.service.LocalGitScanner;
import com.example.autodoc.service.RemoteGitService;
import com.example.autodoc.service.SddGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@Slf4j
@RestController
@RequestMapping("/api/v1/docs")
public class DocController {

    private final LocalGitScanner scanner;
    private final SddGenerator generator;
    private final RemoteGitService remoteGit;

    @Autowired
    JiraService jiraService;

    // Use constructor injection (Best Practice over @Autowired)
    public DocController(LocalGitScanner scanner, SddGenerator generator, RemoteGitService remoteGit) {
        this.scanner = scanner;
        this.generator = generator;
        this.remoteGit = remoteGit;
    }

    /**
     * Build an SDD from a remote repository URL.
     * Produces text/markdown for better rendering in compatible clients.
     */
    @PostMapping(value = "/build-sdd", produces = "text/markdown")
    public ResponseEntity<String> buildSddFromRemote(@RequestParam String repoUrl) {
        //log.info("Request received to generate SDD for: {}", repoUrl);

        try {
            // Step 1: Clone remote to temp and extract content
            String code = remoteGit.fetchRemoteCode(repoUrl);

            if (code == null || code.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("The repository appears to be empty or contains no supported files.");
            }

            // Step 2: Generate SDD using AI
            String sdd = generator.generateSdd(code);

            return ResponseEntity.ok(sdd);

        } catch (IllegalArgumentException e) {
            //log.error("Invalid repository URL: {}", repoUrl, e);
            return ResponseEntity.badRequest().body("Invalid URL: " + e.getMessage());
        } catch (Exception e) {
            //log.error("Internal error during SDD generation for: {}", repoUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate documentation. Please check server logs.");
        }
    }

    @GetMapping("/build-sdd")
    public String build(@RequestParam String repoUrl, @RequestParam String jiraKey) throws Exception {
        // 1. Fetch Requirements from Jira
        String jiraContext = jiraService.fetchIssueRequirements(jiraKey);

        // 2. Fetch Code from Remote Git
        String code = remoteGit.fetchRemoteCode(repoUrl);

        // 3. Send both to AI
        String prompt = String.format("""
        System: You are an architect validating code against requirements.
        
        %s
        
        ### IMPLEMENTED CODE:
        %s
        
        Task: Create an SDD update. Compare if the code fulfills the Jira requirement. 
        Identify any missing logic or security risks.
        """, jiraContext, code);

        return generator.generateSdd(prompt);
    }
}