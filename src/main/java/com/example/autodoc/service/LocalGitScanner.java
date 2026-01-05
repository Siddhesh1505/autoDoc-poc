package com.example.autodoc.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class LocalGitScanner {

    public String getFullCodebase(String path) throws Exception {
        StringBuilder content = new StringBuilder();
        File gitFolder = new File(path, ".git");

        try (Git git = Git.open(gitFolder)) {
            Repository repo = git.getRepository();
            ObjectId head = repo.resolve("HEAD");

            try (RevWalk walk = new RevWalk(repo);
                 TreeWalk treeWalk = new TreeWalk(repo)) {

                RevCommit commit = walk.parseCommit(head);
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    String fileName = treeWalk.getPathString();
                    // We only want source code to save AI tokens
                    if (fileName.endsWith(".java") || fileName.endsWith(".sql")) {
                        byte[] bytes = repo.open(treeWalk.getObjectId(0)).getBytes();
                        content.append("\n\n/* FILE: ").append(fileName).append(" */\n");
                        content.append(new String(bytes, StandardCharsets.UTF_8));
                    }
                }
            }
        }
        return content.toString();
    }
}