**Enterprise Software Design Document (SDD) for Autodoc Application**

**Introduction and Architecture**
=====================================

The Autodoc application is a Spring Boot-based system designed to generate Enterprise Software Design Documents (SDDs) from remote Git repositories. The architecture of the application is based on the Microservices pattern, with each service responsible for a specific task.

*   **RemoteGitService**: Responsible for cloning and extracting code from remote Git repositories.
*   **JiraService**: Handles interactions with Jira to fetch issue requirements and stories.
*   **SddGenerator**: Utilizes AI-powered chat clients to generate SDDs based on the extracted code and Jira requirements.
*   **LocalGitScanner**: Scans local Git repositories for source code.

**Tech Stack**
==============

The application uses the following technologies:

*   **Spring Boot**: As the underlying framework for building the microservices.
*   **Eclipse JGit**: For interacting with Git repositories.
*   **Jackson**: For JSON serialization and deserialization.
*   **WebClient**: For making HTTP requests to external services (e.g., Jira).
*   **Chat Client**: Utilized by SddGenerator to interact with AI-powered chat clients.

**API Design**
==============

The application exposes the following APIs:

*   **/api/v1/docs/build-sdd**: Generates an SDD from a remote Git repository URL.
*   **/api/v1/test/jira/stories/{projectKey}**: Tests Jira connection by fetching stories for a given project key.

**Component Logic**
==================

The application consists of the following components:

*   **RemoteGitService**: Clones and extracts code from remote Git repositories using Eclipse JGit.
*   **JiraService**: Interacts with Jira to fetch issue requirements and stories using WebClient.
*   **SddGenerator**: Utilizes AI-powered chat clients to generate SDDs based on extracted code and Jira requirements.

**Operational Guide**
=====================

To run the application, follow these steps:

1.  Clone the repository using Git.
2.  Run the `mvn spring-boot:run` command to start the application.
3.  Use a tool like Postman or cURL to send requests to the exposed APIs.

**Change Log**
==============

The following changes were made since the last release:

*   Added support for cloning and extracting code from remote Git repositories using Eclipse JGit.
*   Implemented interactions with Jira to fetch issue requirements and stories using WebClient.
*   Utilized AI-powered chat clients to generate SDDs based on extracted code and Jira requirements.

**Security Implementation**
==========================

The application follows best practices for security:

*   Uses HTTPS connections for external services (e.g., Jira).
*   Authenticates with Jira using API tokens.
*   Validates user input to prevent SQL injection attacks.

**Testing**
===========

The application includes unit tests and integration tests to ensure its functionality and reliability.