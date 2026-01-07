package com.example.autodoc.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SddGenerator {

    private final ChatClient chatClient;

    @Autowired
    RemoteGitService remoteGitService;


    public SddGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateSdd(String codebase) {
        // 1. Extract technical context from local files
        String pomContent = remoteGitService.getPomContent(codebase);
//        String currentCode = gitScanner.getLatestSourceCode(repoPath);
//        String gitDiff = diffService.getCommitDiff(repoPath);

        // 2. Structured Prompt for the "Deep Scan"
        String prompt = """
    You are a Lead Software Architect. Create an Enterprise Software Design Document (SDD) by analyzing the provided code.
    
    Structure the document as follows:
    1. Introduction & Architecture (Identify patterns like MVC/Microservices).
    2. Tech Stack (List versions from pom.xml).
    3. API Design (Table of endpoints with example JSON payloads).
    4. Component Logic (Explain the core Service classes).
    5. Operational Guide (Local run commands and Env Var requirements).
    6. Change Log (Summarize the latest Git Diff).
    
    Use Mermaid.js syntax for any sequence or class diagrams where applicable.
    """;

        return chatClient.prompt()
                .system(prompt)
                .user("Analyze this codebase and create the SDD:\n\n" + codebase)
                .call()
                .content();
    }
}
