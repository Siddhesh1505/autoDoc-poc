package com.example.autodoc.service;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class DiffService {
    public String getCommitDiff(String repoPath) throws Exception {
        try (Git git = Git.open(new File(repoPath))) {
            Repository repository = git.getRepository();
            // Get the two most recent commits
            ObjectId head = repository.resolve("HEAD^{tree}");
            ObjectId previous = repository.resolve("HEAD~1^{tree}");

            //Test

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                oldTreeIter.reset(reader, previous);
            }

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                newTreeIter.reset(reader, head);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .setOutputStream(out)
                    .call();

            return out.toString();
        }
    }
}
