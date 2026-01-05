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
                .system("You are a Technical Architect. Analyze the provided code and generate a Software Design Document (SDD).")
                .user("Codebase:\n" + codebase + "\n\nRequirements: Provide Architecture, Data Model, and API Endpoints.")
                .call()
                .content();
    }
}
