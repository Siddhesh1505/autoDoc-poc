package com.example.autodoc.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
public class SourceAggregatorService {

    public String aggregateProject(String repoPath) {
        StringBuilder sb = new StringBuilder();
        File root = new File(repoPath);

        // Start the simple recursive scan
        scanDirectory(root, sb, repoPath);

        return sb.toString();
    }

    private void scanDirectory(File directory, StringBuilder sb, String rootPath) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String path = file.getAbsolutePath();

            // 1. SIMPLE SKIP: Ignore the big/hidden stuff immediately
            if (path.contains(".git") || path.contains("target") || path.contains(".idea")) {
                continue;
            }

            if (file.isDirectory()) {
                scanDirectory(file, sb, rootPath);
            } else if (isCodeFile(file.getName())) {
                try {
                    // 2. FAIL-SAFE: If the path is too long, just skip this one file
                    if (path.length() > 240) continue;

                    String relativePath = path.substring(rootPath.length());
                    sb.append("\n--- FILE: ").append(relativePath).append(" ---\n");
                    sb.append(new String(Files.readAllBytes(file.toPath())));
                    sb.append("\n");
                } catch (Exception e) {
                    // Just move to the next file if one fails
                }
            }
        }
    }

    private boolean isCodeFile(String name) {
        return name.endsWith(".java") || name.endsWith(".xml") || name.endsWith(".properties");
    }
}