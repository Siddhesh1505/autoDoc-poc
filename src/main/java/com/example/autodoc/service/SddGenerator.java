package com.example.autodoc.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SddGenerator {

    private final ChatClient chatClient;

    @Autowired
    RemoteGitService remoteGitService;

    @Autowired
    DiffService diffService;

    @Autowired
    SourceAggregatorService aggregatorService;


    public SddGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateSdd(String repoPath) throws Exception {


        String prompt = String.format("""
        Act as a Principal Software Architect. Your task is to generate a comprehensive, 
        Enterprise-Grade Software Design Document (SDD) based on the provided code.

        ### DOCUMENT STRUCTURE:
        
        ## 1. EXECUTIVE SUMMARY
        - Briefly describe the system's core value and architectural style.
        
        ## 2. TECHNICAL STACK
        - List languages, frameworks, and specific libraries identified.
        
        ## 3. COMPONENT DESIGN & LOGIC
        - Explain the responsibility of each class.
        - Describe the data flow between components.
        
        ## 4. API SPECIFICATIONS
        - Generate a professional table of endpoints.
        - Provide sample JSON Request/Response payloads.
        
        ## 5. QUALITY & SECURITY
        - Identify potential edge cases for testing.
        - Describe the security implementation for credentials and data.
        
        ## 6. DEPLOYMENT & LOCAL RUN
        - Provide the shell commands for building and running.

        ### CODE CONTEXT TO ANALYZE:
        %s
        
        Format the output in clean Markdown with professional headings.
        """, repoPath);

        return chatClient.prompt()
                .system(prompt)
                .user("Analyze this codebase and create the SDD:\n\n" + repoPath)
                .call()
                .content();
    }
}
