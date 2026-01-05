package com.example.autodoc.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SddGenerator {

    private final ChatClient chatClient;

    public SddGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateSdd(String codebase) {
        return chatClient.prompt()
                .system("""
            You are a Senior Technical Architect. 
            Analyze the provided Java source code and generate a Software Design Document (SDD).
            
            STRICT RULES:
            1. Use ONLY the provided code. Do not invent features.
            2. If you don't see a database, state 'No Database Found'.
            3. Follow this Markdown structure exactly:
               # SDD: [Project Name]
               ## 1. System Overview
               ## 2. Component Architecture
               ## 3. Data Flow
               ## 4. API Endpoints
            4. Be concise and technical.
            """)
                .user("Analyze this codebase and create the SDD:\n\n" + codebase)
                .call()
                .content();
    }
}
