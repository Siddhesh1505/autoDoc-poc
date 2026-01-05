# SDD: Autodoc Application
## 1. System Overview
The Autodoc application is a Spring Boot-based system designed to generate Software Design Documents (SDD) from Java source code. It utilizes AI-powered chat clients for generating the SDD.

### Key Components:

*   **AiConfig**: Configures the AI-powered chat client.
*   **DocController**: Handles API requests for building SDDs from remote repositories or local file systems.
*   **RemoteGitService**: Clones and fetches code from remote Git repositories.
*   **LocalGitScanner**: Scans local Git repositories for source code files.

## 2. Component Architecture
The system consists of the following components:

### Service Layer

*   **AiConfig**: Provides a configured AI-powered chat client instance.
*   **RemoteGitService**: Clones and fetches code from remote Git repositories.
*   **LocalGitScanner**: Scans local Git repositories for source code files.

### Controller Layer

*   **DocController**: Handles API requests for building SDDs from remote repositories or local file systems.

## 3. Data Flow
The system follows the following data flow:

1.  The user sends a request to build an SDD using the `buildSddFromRemote` method in the `DocController`.
2.  The `RemoteGitService` clones and fetches code from the specified remote Git repository.
3.  The fetched code is then passed to the `SddGenerator` for generating the SDD.
4.  The generated SDD is returned as a response to the user.

## 4. API Endpoints
The system exposes the following API endpoints:

*   **/api/v1/docs/build-sdd**: Builds an SDD from a remote repository URL.

### Request Parameters

*   `repoUrl`: The URL of the remote Git repository.

### Response

*   The generated SDD as text/markdown content.
