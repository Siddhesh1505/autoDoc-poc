package com.example.autodoc.service;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Service
public class RemoteGitService {

    public String fetchRemoteCode(String remoteUrl) throws Exception {
        Path tempDir = Files.createTempDirectory("autodoc_repo_");

        // Use a try-with-resources to ensure the Git object is closed properly
        try (Git git = Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(tempDir.toFile())
                .setCloneAllBranches(false)
                .call()) { // .call() triggers the actual download

            //log.info("Repository cloned to: {}", tempDir.toAbsolutePath());

            // IMPORTANT: Verify files exist before scanning
            File[] files = tempDir.toFile().listFiles();
            if (files == null || files.length <= 1) { // 1 because .git folder always exists
                throw new RuntimeException("Clone successful but no files were found in " + tempDir);
            }

            return scanDirectory(tempDir.toFile());
        } finally {
            // Optional: Keep files temporarily for debugging, then delete
            // deleteDirectory(tempDir);
        }
    }

    private String scanDirectory(File folder) throws IOException {
        StringBuilder content = new StringBuilder();
        Files.walk(folder.toPath())
                .filter(path -> Files.isRegularFile(path))
                .filter(path -> path.toString().endsWith(".java") || path.toString().endsWith(".yml"))
                // Ignore common boilerplate folders to save AI tokens
                .filter(path -> !path.toString().contains("/target/") && !path.toString().contains("/.git/"))
                .forEach(path -> {
                    try {
                        content.append("\n/* FILE: ").append(path.getFileName()).append(" */\n");
                        content.append(Files.readString(path));
                    } catch (IOException e) {
                        //log.error("Failed to read file: {}", path);
                    }
                });
        return content.toString();
    }

    public String getPomContent(String repoPath) {
        try {
            Path pomPath = Paths.get(repoPath, "pom.xml");
            return Files.readString(pomPath);
        } catch (IOException e) {
            return "pom.xml not found";
        }
    }
}
