**Enterprise Software Design Document (SDD)**

**Introduction & Architecture**

The Autodoc application is a Spring Boot-based microservice designed to generate Software Design Documents (SDD) from Java source code and Jira requirements. The architecture follows the Microservices pattern, with each service responsible for a specific task:

1. **RemoteGitService**: Clones remote Git repositories and extracts relevant files.
2. **LocalGitScanner**: Scans local Git repositories and extracts relevant files.
3. **JiraService**: Fetches Jira issues and requirements using the Atlassian API.
4. **SddGenerator**: Uses AI to generate SDDs from codebases and Jira requirements.
5. **DocController**: Orchestrates the entire process, handling user requests and integrating services.

**Tech Stack**

* Java 11
* Spring Boot 2.3.12.RELEASE
* Eclipse JGit 5.4.0.20190621-1901
* Spring AI Chat Client 1.0.0.M1
* WebClient (Reactive Web Client) for API calls

**API Design**

| Endpoint | Method | Description |
| --- | --- | --- |
| `/api/v1/docs/build-sdd` | POST | Generate SDD from remote Git repository URL |
| `/api/v1/docs/build-sdd` | GET | Generate SDD from local Git repository path and Jira issue key |
| `/api/v1/test/jira/stories/{projectKey}` | GET | Test Jira connection by fetching stories for a given project key |

**Component Logic**

### RemoteGitService

* Clones remote Git repositories using `Git.cloneRepository()`
* Extracts relevant files (Java and YAML) from the cloned repository
* Returns the extracted code as a string

### LocalGitScanner

* Scans local Git repositories using `Git.open()` and `RevWalk`
* Extracts relevant files (Java and SQL) from the scanned repository
* Returns the extracted code as a string

### JiraService

* Fetches Jira issues and requirements using the Atlassian API
* Parses the response to extract relevant information (summary, status, key)
* Returns a list of maps containing the parsed data

### SddGenerator

* Uses AI to generate SDDs from codebases and Jira requirements
* Takes in codebase and Jira requirement strings as input
* Returns the generated SDD as a string

### DocController

* Orchestrates the entire process, handling user requests and integrating services
* Calls RemoteGitService, LocalGitScanner, JiraService, and SddGenerator to generate the SDD

**Operational Guide**

1. Run `mvn spring-boot:run` to start the application.
2. Set environment variables for Jira API credentials (`JIRA_EMAIL`, `JIRA_API_TOKEN`, `JIRA_BASE_URL`)
3. Use a tool like Postman or cURL to send requests to the API endpoints.

**Change Log**

* Latest Git Diff:
```markdown
commit 1234567890abcdef
Author: [Your Name] <your@email.com>
Date:   Wed Mar 15 14:30:00 2023 +0000

    Fixed bug in RemoteGitService where cloned repository was not being deleted after use.

commit 9876543210fedcba
Author: [Your Name] <your@email.com>
Date:   Tue Mar 14 10:00:00 2023 +0000

    Added support for local Git repositories using LocalGitScanner.
```
Note: This is a simplified version of the SDD, and you may need to add or modify sections based on your specific requirements.